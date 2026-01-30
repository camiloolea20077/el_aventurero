import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private _isVisible = new BehaviorSubject<boolean>(true);
  private _isCollapsed = new BehaviorSubject<boolean>(false);

  public isVisible$ = this._isVisible.asObservable();
  public isCollapsed$ = this._isCollapsed.asObservable();

  constructor() {
    // Verificar si es móvil al inicializar
    if (this.isMobile()) {
      this._isVisible.next(false);
    }
  }

  get isVisible(): boolean {
    return this._isVisible.value;
  }

  get isCollapsed(): boolean {
    return this._isCollapsed.value;
  }
  // 👇 NUEVO
  setVisible(value: boolean): void { this._isVisible.next(value); }
  setCollapsed(value: boolean): void { this._isCollapsed.next(value); }
  
  toggleSidebar(): void {
    this._isVisible.next(!this._isVisible.value);
  }

  toggleCollapsed(): void {
    this._isCollapsed.next(!this._isCollapsed.value);
  }

  closeSidebar(): void {
    this._isVisible.next(false);
  }

  openSidebar(): void {
    this._isVisible.next(true);
  }

  private isMobile(): boolean {
    return window.innerWidth <= 768;
  }
}