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
import {
  APP_PERMISSION_OPTIONS,
  resolveLandingRoute,
} from '../../constants/permissions';

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
    const allItems: MenuItem[] = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        route: '/dashboard',
        exact: true,
        permissions: [
          AppPermissions.DASHBOARD_ACCESS,
          AppPermissions.ADMIN_ACCESS,
        ],
      },
      // --- OPERACIONES DEL BAR ---
      {
        label: 'Operaciones',
        icon: 'pi pi-cog',
        permissions: [
          AppPermissions.MESAS_ACCESS,
          AppPermissions.SALES_ACCESS,
          AppPermissions.ADMIN_ACCESS,
        ],
        children: [
          {
            label: 'Mesas',
            icon: 'pi pi-table',
            route: '/mesas',
            permissions: [
              AppPermissions.MESAS_ACCESS,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
          {
            label: 'Ventas',
            icon: 'pi pi-dollar',
            route: '/sales',
            permissions: [
              AppPermissions.SALES_ACCESS,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
        ],
      },
      // --- GESTIÓN DE PRODUCTOS ---
      {
        label: 'Productos',
        icon: 'pi pi-box',
        permissions: [
          AppPermissions.PRODUCTS_ACCESS,
          AppPermissions.INVENTORY_VIEW,
          AppPermissions.INVENTORY_COUNT_ACCESS,
          AppPermissions.ADMIN_ACCESS,
        ],
        children: [
          {
            label: 'Catálogo',
            icon: 'pi pi-barcode',
            route: '/products',
            permissions: [
              AppPermissions.PRODUCTS_ACCESS,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
          {
            label: 'Inventario',
            icon: 'pi pi-list',
            route: '/inventory',
            permissions: [
              AppPermissions.INVENTORY_VIEW,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
          {
            label: 'Conteo de Inventario',
            icon: 'pi pi-calculator',
            route: '/conteo-inventario',
            permissions: [
              AppPermissions.INVENTORY_COUNT_ACCESS,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
        ],
      },
      // --- COMPRAS Y PROVEEDORES ---
      {
        label: 'Compras',
        icon: 'pi pi-shopping-cart',
        route: '/shopping',
        permissions: [
          AppPermissions.SHOPPING_ACCESS,
          AppPermissions.ADMIN_ACCESS,
        ],
      },
      // --- CAJA Y FINANZAS ---
      {
        label: 'Caja',
        icon: 'pi pi-wallet',
        permissions: [
          AppPermissions.CAJA_ACCESS,
          AppPermissions.FLUJO_CAJA_ACCESS,
          AppPermissions.ARQUEO_CAJA_ACCESS,
          AppPermissions.ADMIN_ACCESS,
        ],
        children: [
          {
            label: 'Flujo de Caja',
            icon: 'pi pi-chart-line',
            route: '/flujo-caja',
            permissions: [
              AppPermissions.FLUJO_CAJA_ACCESS,
              AppPermissions.CAJA_ACCESS,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
          {
            label: 'Arqueo de Caja',
            icon: 'pi pi-calculator',
            route: '/arqueo-caja',
            permissions: [
              AppPermissions.ARQUEO_CAJA_ACCESS,
              AppPermissions.CAJA_ACCESS,
              AppPermissions.ADMIN_ACCESS,
            ],
          },
        ],
      },
    ];

    this.items = this.filterMenuByPermissions(allItems);
  }

  private filterMenuByPermissions(items: MenuItem[]): MenuItem[] {
    return items
      .map((item) => {
        if (item.children) {
          const filteredChildren = this.filterMenuByPermissions(item.children);
          if (filteredChildren.length > 0) {
            return { ...item, children: filteredChildren };
          }
        }
        if (
          !item.permissions ||
          this.permissionService.hasAny(item.permissions)
        ) {
          const { permissions, ...itemWithoutPermissions } = item;
          return itemWithoutPermissions;
        }
        return null;
      })
      .filter((item): item is MenuItem => item !== null);
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
