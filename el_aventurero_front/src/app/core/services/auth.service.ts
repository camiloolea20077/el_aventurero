import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';


import { IndexDBService } from './index-db.service';
import { environment } from '../../../environments/environment';
import { ResponseModel } from '../../../shared/models/responde.models';
import { AuthResponse, LoginDto } from '../../modules/mesas/interfaces/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private router: Router,
    private indexDBService: IndexDBService
  ) {}

  // Iniciar sesión y guardar auth en IndexedDB
  login(credentials: LoginDto): Observable<ResponseModel<AuthResponse>> {
    return this.http.post<ResponseModel<AuthResponse>>(
      `${this.apiUrl}security/login`,
      credentials
    ).pipe(
      tap((response) => {
        this.indexDBService.saveAuthData(response.data).then(() => {
          this.router.navigate(['/dashboard']);
        }).catch((err) => {
          console.error('❌ Error guardando en IndexedDB', err);
        });
      })
    );
  }

  // Cerrar sesión
  logout(): void {
    this.indexDBService.deleteDataAuthDB().then(() => {
      this.router.navigate(['/login']);
    });
  }

  // Verificar si el usuario está autenticado
  isAuthenticated(): Observable<boolean> {
    return new Observable<boolean>((observer) => {
      this.indexDBService.loadDataAuthDB().then((authResponse) => {
        observer.next(!!authResponse?.token);
        observer.complete();
      }).catch(() => {
        observer.next(false);
        observer.complete();
      });
    });
  }

  // Obtener solo el token
  getToken(): Observable<string | null> {
    return new Observable<string | null>((observer) => {
      this.indexDBService.getToken().then((token) => {
        observer.next(token);
        observer.complete();
      }).catch(() => {
        observer.next(null);
        observer.complete();
      });
    });
  }

  // Obtener el AuthResponse completo
  getAuthResponse(): Observable<AuthResponse | null> {
    return new Observable<AuthResponse | null>((observer) => {
      this.indexDBService.loadDataAuthDB().then((authResponse) => {
        observer.next(authResponse);
        observer.complete();
      }).catch(() => {
        observer.next(null);
        observer.complete();
      });
    });
  }

  // Verificar si el token ha expirado
  isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    const expiry = payload.exp;
    const now = Math.floor(Date.now() / 1000);
    return expiry < now;
  }

  // Decodificar el token JWT
  private decodeToken(token: string): any {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  }
}
