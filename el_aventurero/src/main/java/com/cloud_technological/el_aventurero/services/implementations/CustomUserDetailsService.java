package com.cloud_technological.el_aventurero.services.implementations;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cloud_technological.el_aventurero.entity.UserEntity;
import com.cloud_technological.el_aventurero.repositories.users.UserJPARepository;



@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserJPARepository userRepository;

    /**
     * Método para cargar los detalles de usuario por nombre de usuario o correo
     * electrónico.
     *
     * @param usernameOrEmail Nombre de usuario o correo electrónico del usuario.
     * @return Detalles del usuario encontrado.
     * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de
     *                                   datos.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Buscar al usuario por su correo electrónico en la base de datos
        UserEntity user = userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado : " + usernameOrEmail));

        // Crear y devolver un UserDetails utilizando los datos del usuario encontrado
        return new User(user.getEmail(), user.getPassword(), getAuthorities());
    }

    /**
     * Método para obtener los roles (authorities) del usuario.
     *
     * @return Colección de authorities del usuario (en este caso, ROLE_USER).
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        return authorities;
    }
}
