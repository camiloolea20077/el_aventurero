import { Routes } from '@angular/router';
import { DummyComponent } from '../shared/components/dummy-component/dummy.component';
import { AuthGuard } from './core/interceptors/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'redirector',
    pathMatch: 'full',
  },
  {
    path: 'redirector',
    canActivate: [AuthGuard],
    component: DummyComponent,
  },
  {
    path: '',
    loadComponent: () =>
      import('../shared/components/layout/layout.component').then(
        (c) => c.LayoutComponent,
      ),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./modules/dashboard/UI/index/index-dashboard.component').then(
            (c) => c.IndexDashboardComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'mesas',
        loadComponent: () =>
          import('./modules/mesas/UI/pages/index-mesas/index-mesas.component').then(
            (m) => m.IndexMesasComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./modules/productos/UI/pages/index-producto.component').then(
            (m) => m.IndexProductoComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./modules/inventario/UI/index/index-inventario.component').then(
            (m) => m.IndexInventarioComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'conteo-inventario',
        loadComponent: () =>
          import('./modules/conteo-inventario/UI/index/index-conteo-inventario.component').then(
            (m) => m.IndexConteoInventarioComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'shopping',
        loadComponent: () =>
          import('./modules/compras/UI/pages/index-compra.component').then(
            (m) => m.IndexCompraComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'sales',
        loadComponent: () =>
          import('./modules/ventas/UI/index/index-venta.component').then(
            (m) => m.IndexVentaComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'flujo-caja',
        loadComponent: () =>
          import('./modules/flujo-caja/UI/pages/index-flujo-caja.component').then(
            (m) => m.IndexFlujoCajaComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'arqueo-caja',
        loadComponent: () =>
          import('./modules/arqueo-caja/UI/pages/index-arqueo-caja.component').then(
            (m) => m.IndexArqueoCajaComponent,
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'users',
        loadChildren: () =>
          import('./modules/users/users.routes').then((m) => m.USERS_ROUTES),
      },
    ],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./core/auth/login/login.component').then((c) => c.LoginComponent),
    canActivate: [AuthGuard],
  },
  {
    path: '**',
    loadComponent: () =>
      import('../shared/components/not-found/not-found.component').then(
        (m) => m.NotFoundComponent,
      ),
  },
];
