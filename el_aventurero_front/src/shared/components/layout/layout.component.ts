import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';

import {
  ActivatedRoute,
  NavigationEnd,
  Router,
  RouterModule,
} from '@angular/router';
import { ToastModule } from 'primeng/toast';

import { MenuItem } from 'primeng/api';
import { filter, map } from 'rxjs';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { SidebarService } from '../../services/sidebar.service';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  imports: [
    NavbarComponent,
    SidebarComponent,
    RouterModule,
    ToastModule,
    BreadcrumbModule,
  ],
})
export class LayoutComponent {
  breadcrumbItems: MenuItem[] = [];
  home: MenuItem = { icon: 'pi pi-home', routerLink: '/dashboard' };
  constructor(
    public sidebarService: SidebarService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {
    this.setupBreadcrumb();
  }

  private setupBreadcrumb() {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => this.activatedRoute),
        map((route) => {
          while (route.firstChild) {
            route = route.firstChild;
          }
          return route;
        }),
      )
      .subscribe(() => {
        this.breadcrumbItems = this.createBreadcrumbs();
      });
  }

  private createBreadcrumbs(): MenuItem[] {
    const url = this.router.url;
    const breadcrumbs: MenuItem[] = [];

    // Mapa de rutas a labels
    const routeLabels: { [key: string]: string } = {
      '/dashboard': 'Dashboard',
      '/mesas': 'Mesas',
      '/products': 'Productos',
      '/inventory': 'Inventario',
      '/shopping': 'Compras',
      '/sales': 'Ventas',
      '/conteo-inventario': 'Conteo de Inventario',
      '/arqueo-caja': 'Arqueo de Caja',
      '/flujo-caja': 'Flujo Caja',
      '/users': 'Usuarios',
      '/admin/roles': 'Roles',
      '/admin/farms': 'Fincas',
    };

    const pathSegments = url.split('/').filter((segment) => segment);
    let currentPath = '';

    pathSegments.forEach((segment, index) => {
      currentPath += `/${segment}`;

      if (routeLabels[currentPath]) {
        breadcrumbs.push({
          label: routeLabels[currentPath],
          routerLink:
            index === pathSegments.length - 1 ? undefined : currentPath,
        });
      }
    });

    return breadcrumbs;
  }
}
