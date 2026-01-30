package com.cloud_technological.el_aventurero.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.cloud_technological.el_aventurero.dto.auth.UserDetailDto;
import com.cloud_technological.el_aventurero.repositories.auth.AuthQueryRepository;
import com.cloud_technological.el_aventurero.util.GlobalException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

    @Autowired
    private AuthQueryRepository authQueryRepository;

    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-milliseconds}")
    private int jwtExpirationInMs;

    private SecretKey jwtSecret;

    @PostConstruct
    public void init() {
        try {
            if (jwtSecretString == null || jwtSecretString.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT Secret cannot be null or empty");
            }

            byte[] keyBytes = Base64.getDecoder().decode(jwtSecretString);
            if (keyBytes.length < 64) {
                throw new IllegalArgumentException("JWT Secret must be at least 64 bytes for HS512");
            }

            jwtSecret = Keys.hmacShaKeyFor(keyBytes);
            System.out.println("✅ JWT Secret initialized with " + keyBytes.length + " bytes");
        } catch (Exception e) {
            System.err.println("❌ Error initializing JWT secret: " + e.getMessage());
            throw e;
        }
    }

    public String generateToken(Authentication authentication, String email) {
        UserDetailDto user = authQueryRepository.findByUserLogin(email);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    public Long getCompanyIdFromToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        System.out.println("🔐 TOKEN RECIBIDO: " + token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("companyId", Long.class);
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new GlobalException(HttpStatus.UNAUTHORIZED, "Token JWT no encontrado o mal formado");
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject() != null;
        } catch (MalformedJwtException ex) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Token JWT no válido");
        } catch (ExpiredJwtException ex) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Token JWT caducado");
        } catch (UnsupportedJwtException ex) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Token JWT no compatible");
        } catch (IllegalArgumentException ex) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Token JWT vacío");
        } catch (SignatureException e) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Firma JWT no coincide");
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
