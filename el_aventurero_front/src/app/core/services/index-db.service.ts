import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthResponse } from '../../modules/mesas/interfaces/auth.model';

@Injectable({ providedIn: 'root' })
export class IndexDBService {
  private readonly AUTH_KEY = 'authResponse';
  private localForage: any = null;
  private initPromise: Promise<void>;

  constructor(@Inject(PLATFORM_ID) private platformId: object) {
    this.initPromise = this.initLocalForage();
  }

  private get isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  private async initLocalForage(): Promise<void> {
    // SSR: no intentamos inicializar nada
    if (!this.isBrowser) return;

    try {
      const module = await import('localforage');
      this.localForage = module.default || module;

      this.localForage.config({
        name: 'app-db',
        version: 1.0,
        storeName: 'authData',
        description: 'Datos de sesión del usuario autenticado',
      });
    } catch (error) {
      // En navegador sí reportamos
      console.error('Error inicializando localForage:', error);
      this.localForage = null;
    }
  }

  private async ensureInitialized(): Promise<boolean> {
    // SSR: simplemente no hay storage
    if (!this.isBrowser) return false;

    await this.initPromise;
    return !!this.localForage;
  }

  async saveAuthData(data: AuthResponse): Promise<void> {
    try {
      const ok = await this.ensureInitialized();
      if (!ok) return;

      await this.localForage.setItem(this.AUTH_KEY, data);
    } catch (error) {
      console.error('Error guardando datos en IndexedDB:', error);
    }
  }

  async loadDataAuthDB(): Promise<AuthResponse | null> {
    try {
      const ok = await this.ensureInitialized();
      if (!ok) return null;

      const data = await this.localForage.getItem(this.AUTH_KEY);
      return (data ?? null) as AuthResponse | null;
    } catch (error) {
      console.error('Error cargando datos de IndexedDB:', error);
      return null;
    }
  }

  async deleteDataAuthDB(): Promise<void> {
    try {
      const ok = await this.ensureInitialized();
      if (!ok) return;

      await this.localForage.removeItem(this.AUTH_KEY);
    } catch (error) {
      console.error('Error eliminando datos de IndexedDB:', error);
    }
  }

  async isAuthenticated(): Promise<boolean> {
    // SSR-safe: devolver false
    if (!this.isBrowser) return false;

    const data = await this.loadDataAuthDB();
    return !!data?.token;
  }

  async getToken(): Promise<string | null> {
    if (!this.isBrowser) return null;

    const data = await this.loadDataAuthDB();
    return data?.token ?? null;
  }

  async getUserId(): Promise<number | null> {
    if (!this.isBrowser) return null;

    const data = await this.loadDataAuthDB();
    return data?.user?.id ?? null;
  }
}
