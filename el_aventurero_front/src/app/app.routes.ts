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
      },
      {
        path: 'mesas',
        loadComponent: () =>
          import('./modules/mesas/UI/pages/index-mesas/index-mesas.component').then(
            (m) => m.IndexMesasComponent,
          ),
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./modules/productos/UI/pages/index-producto.component').then(
            (m) => m.IndexProductoComponent,
          ),
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./modules/inventario/UI/index/index-inventario.component').then(
            (m) => m.IndexInventarioComponent,
          ),
      },
      {
        path: 'shopping',
        loadComponent: () =>
          import('./modules/compras/UI/pages/index-compra.component').then(
            (m) => m.IndexCompraComponent,
          ),
      },
      {
        path: 'sales',
        loadComponent: () =>
          import('./modules/ventas/UI/index/index-venta.component').then(
            (m) => m.IndexVentaComponent,
          ),
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
