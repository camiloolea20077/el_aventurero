import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  OnInit,
  OnDestroy,
  HostListener,
  Inject,
  PLATFORM_ID,
} from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { TooltipModule } from 'primeng/tooltip';
import { trigger, style, transition, animate } from '@angular/animations';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { MenuItem } from '../../interfaces/menu-interface';
import { AppPermissions } from '../../interfaces/permissions.enum';
import { SidebarService } from '../../services/sidebar.service';
import { IndexDBService } from '../../../app/core/services/index-db.service';
import { AuthService } from '../../../app/core/services/auth.service';
import { PermissionService } from '../../../app/core/services/permission.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  imports: [
    RouterLink,
    CommonModule,
    ToastModule,
    RouterModule,
    ButtonModule,
    RippleModule,
    TooltipModule,
    ConfirmDialogModule,
  ],
  animations: [
    trigger('slideDown', [
      transition(':enter', [
        style({ height: '0', opacity: 0 }),
        animate('200ms ease-in-out', style({ height: '*', opacity: 1 })),
      ]),
      transition(':leave', [
        animate('200ms ease-in-out', style({ height: '0', opacity: 0 })),
      ]),
    ]),
  ],
  providers: [ConfirmationService, MessageService],
})
export class SidebarComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  userName: string = '';
  userRole: string = '';
  items: MenuItem[] = [];
  expandedItems: Record<string, boolean> = {};
  isMobile = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: object,
    private indexDBService: IndexDBService,
    private authService: AuthService,
    private permissionService: PermissionService,
    public sidebarService: SidebarService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private router: Router,
  ) {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  async ngOnInit(): Promise<void> {
    this.checkScreenSize();

    await this.loadUserData();
    this.loadMenuItems();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private async loadUserData() {
    try {
      const authData = await this.indexDBService.loadDataAuthDB();
      if (authData?.user) {
        this.userName = authData.user.name || authData.user.email || 'Usuario';
        this.userRole = authData.user.role || 'Administrador';
      }
    } catch (error) {
      console.error('Error cargando datos del usuario:', error);
      this.userName = 'Usuario';
      this.userRole = 'Administrador';
    }
  }

  private checkScreenSize(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.isMobile = false;
      return;
    }

    this.isMobile = window.innerWidth <= 768;

    if (this.isMobile) {
      this.sidebarService.setCollapsed(false);
      this.sidebarService.setVisible(false);
    } else {
      this.sidebarService.setVisible(true);
    }
  }

  private loadMenuItems(): void {
    this.authService
      .getAuthResponse()
      .pipe(takeUntil(this.destroy$))
      .subscribe((auth) => {
        const permisos = auth?.user?.permisos ?? [];
        this.permissionService.setPermissions(permisos);
        this.buildMenu();
      });
  }

  private buildMenu(): void {
    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        route: '/dashboard',
        exact: true,
      },
      { label: 'Mesas', icon: 'pi pi-table', route: '/mesas' },
      { label: 'Productos', icon: 'pi pi-barcode', route: '/products' },
      { label: 'Inventario', icon: 'pi pi-box', route: '/inventory' },
      { label: 'Compras', icon: 'pi pi-shopping-cart', route: '/shopping' },
      { label: 'ventas', icon: 'pi pi-dollar', route: '/sales' },
      { label: 'Usuarios', icon: 'pi pi-users', route: '/users' },
    ];
  }

  // ... (resto de tu código de permisos y acciones igual)

  toggleSidebar(): void {
    this.sidebarService.toggleSidebar();
  }

  closeSidebar(): void {
    if (this.isMobile) {
      this.sidebarService.closeSidebar();
    }
  }

  toggleExpanded(): void {
    this.sidebarService.toggleCollapsed();
    if (this.sidebarService.isCollapsed) {
      this.expandedItems = {};
    }
  }

  toggleItem(label: string): void {
    if (this.sidebarService.isCollapsed) {
      this.sidebarService.toggleCollapsed();
    }
    this.expandedItems[label] = !this.expandedItems[label];
  }

  isItemExpanded(label: string): boolean {
    return !!this.expandedItems[label];
  }

  trackByFn(index: number, item: MenuItem): string {
    return item.label;
  }

  trackByChildFn(index: number, child: MenuItem): string {
    return child.label;
  }

  logout() {
    this.confirmationService.confirm({
      message: '¿Está seguro que desea cerrar la sesión?',
      header: 'Confirmar cierre de sesión',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, cerrar sesión',
      rejectLabel: 'Cancelar',
      accept: async () => {
        try {
          await this.indexDBService.deleteDataAuthDB();
          this.messageService.add({
            severity: 'success',
            summary: 'Sesión cerrada',
            detail: 'Ha cerrado sesión correctamente',
          });
          this.router.navigate(['/login']);
        } catch (error) {
          console.error('Error cerrando sesión:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Error al cerrar la sesión',
          });
        }
      },
    });
  }
}
