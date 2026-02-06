import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { resolveLandingRoute } from '../../../shared/constants/permissions';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    const isLoginRoute = state.url === '/login';
    const isRootRoute = state.url === '/';

    return this.authService.getAuthResponse().toPromise().then((authResponse) => {
      const token = authResponse?.token;
      const isExpired = !token || this.authService.isTokenExpired(token);
      const permisos = authResponse?.user?.permisos ?? [];
      const landingRoute = resolveLandingRoute(permisos);

      // Si está en raíz '/'
      if (isRootRoute) {
        if (!isExpired) {
          this.router.navigate([landingRoute]);
        } else {
          this.router.navigate(['/login']);
        }
        return false; // Siempre bloquear, para que no cargue nada visual
      }

      // Si token expirado
      if (isExpired) {
        if (!isLoginRoute) {
          this.router.navigate(['/login']);
          return false;
        }
        return true; // Permite acceder al login
      }

      // Token válido
      if (isLoginRoute) {
        this.router.navigate([landingRoute]);
        return false;
      }

      return true; // Permite acceso normal
    });
  }
}
