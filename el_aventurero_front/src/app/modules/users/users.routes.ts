import { Routes } from '@angular/router';

export const USERS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./UI/page/index/index-users.component').then(
        (m) => m.IndexUsersComponent,
      ),
  },
  {
    path: 'create',
    data: {
      slug: 'create',
      title: 'Nuevo Usuario',
    },
    loadComponent: () =>
      import('./UI/components/form/form-users.component').then(
        (m) => m.FormUsersComponent,
      ),
  },
  {
    path: 'edit/:id',
    data: {
      slug: 'edit',
      title: 'Editar Empleado',
    },
    loadComponent: () =>
      import('./UI/components/form/form-users.component').then(
        (m) => m.FormUsersComponent,
      ),
  },
];
