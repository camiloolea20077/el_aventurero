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
        path: 'mesas',
        loadComponent: () =>
          import('./modules/mesas/UI/pages/index-mesas/index-mesas.component').then(
            (m) => m.IndexMesasComponent,
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
