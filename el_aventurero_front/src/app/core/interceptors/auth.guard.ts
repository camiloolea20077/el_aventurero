import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';


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

    // Si está en raíz '/'
    if (isRootRoute) {
      if (!isExpired) {
        this.router.navigate(['/dashboard']);
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
      this.router.navigate(['/dashboard']);
      return false;
    }

    return true; // Permite acceso normal
  });
}

}
