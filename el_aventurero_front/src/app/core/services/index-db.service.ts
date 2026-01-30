import { Injectable } from '@angular/core';
import { AuthResponse } from '../../modules/mesas/interfaces/auth.model';

@Injectable({ providedIn: 'root' })
export class IndexDBService {
  private readonly AUTH_KEY = 'authResponse';
  private localForage: any = null;
  private initPromise: Promise<void>;

  constructor() {
    this.initPromise = this.initLocalForage();
  }

  private async initLocalForage(): Promise<void> {
    if (typeof window !== 'undefined') {
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
        console.error('Error inicializando localForage:', error);
      }
    }
  }

  private async ensureInitialized(): Promise<void> {
    await this.initPromise;
    if (!this.localForage) {
      throw new Error('LocalForage no está disponible');
    }
  }

  async saveAuthData(data: AuthResponse): Promise<void> {
    try {
      await this.ensureInitialized();
      await this.localForage.setItem(this.AUTH_KEY, data);
    } catch (error) {
      console.error('Error guardando datos en IndexedDB:', error);
    }
  }

  async loadDataAuthDB(): Promise<AuthResponse | null> {
    try {
      await this.ensureInitialized();
      const data = await this.localForage.getItem(this.AUTH_KEY);
      return data ?? null;
    } catch (error) {
      console.error('Error cargando datos de IndexedDB:', error);
      return null;
    }
  }

  async deleteDataAuthDB(): Promise<void> {
    try {
      await this.ensureInitialized();
      await this.localForage.removeItem(this.AUTH_KEY);
    } catch (error) {
      console.error('Error eliminando datos de IndexedDB:', error);
    }
  }

  async isAuthenticated(): Promise<boolean> {
    const data = await this.loadDataAuthDB();
    return !!data?.token;
  }

  async getToken(): Promise<string | null> {
    const data = await this.loadDataAuthDB();
    return data?.token ?? null;
  }

  async getUserId(): Promise<number | null> {
    const data = await this.loadDataAuthDB();
    return data?.user?.id ?? null;
  }
}
