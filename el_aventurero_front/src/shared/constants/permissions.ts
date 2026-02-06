import { AppPermissions } from '../interfaces/permissions.enum';

export interface PermissionOption {
  label: string;
  value: AppPermissions;
}

export const APP_PERMISSION_OPTIONS: PermissionOption[] = [
  { label: 'Acceso a Administracion', value: AppPermissions.ADMIN_ACCESS },

  { label: 'Acceso a Dashboard', value: AppPermissions.DASHBOARD_ACCESS },
  { label: 'Acceso a Mesas', value: AppPermissions.MESAS_ACCESS },
  { label: 'Acceso a Ventas', value: AppPermissions.SALES_ACCESS },
  { label: 'Acceso a Productos', value: AppPermissions.PRODUCTS_ACCESS },
  { label: 'Acceso a Inventario', value: AppPermissions.INVENTORY_VIEW },
  {
    label: 'Acceso a Conteo de Inventario',
    value: AppPermissions.INVENTORY_COUNT_ACCESS,
  },
  { label: 'Acceso a Compras', value: AppPermissions.SHOPPING_ACCESS },

  { label: 'Acceso a Caja (general)', value: AppPermissions.CAJA_ACCESS },
  { label: 'Acceso a Flujo de Caja', value: AppPermissions.FLUJO_CAJA_ACCESS },
  { label: 'Acceso a Arqueo de Caja', value: AppPermissions.ARQUEO_CAJA_ACCESS },

  { label: 'Acceso a Usuarios', value: AppPermissions.USERS_ACCESS },
];

export const DEFAULT_ROUTE_BY_PERMISSION: Partial<Record<AppPermissions, string>> = {
  [AppPermissions.DASHBOARD_ACCESS]: '/dashboard',
  [AppPermissions.MESAS_ACCESS]: '/mesas',
  [AppPermissions.SALES_ACCESS]: '/sales',
  [AppPermissions.CAJA_ACCESS]: '/flujo-caja',
  [AppPermissions.FLUJO_CAJA_ACCESS]: '/flujo-caja',
  [AppPermissions.ARQUEO_CAJA_ACCESS]: '/arqueo-caja',
  [AppPermissions.PRODUCTS_ACCESS]: '/products',
  [AppPermissions.INVENTORY_VIEW]: '/inventory',
  [AppPermissions.INVENTORY_COUNT_ACCESS]: '/conteo-inventario',
  [AppPermissions.SHOPPING_ACCESS]: '/shopping',
  [AppPermissions.USERS_ACCESS]: '/users',
};

const LANDING_PRIORITY: AppPermissions[] = [
  AppPermissions.DASHBOARD_ACCESS,
  AppPermissions.MESAS_ACCESS,
  AppPermissions.SALES_ACCESS,
  AppPermissions.CAJA_ACCESS,
  AppPermissions.FLUJO_CAJA_ACCESS,
  AppPermissions.ARQUEO_CAJA_ACCESS,
  AppPermissions.PRODUCTS_ACCESS,
  AppPermissions.INVENTORY_VIEW,
  AppPermissions.INVENTORY_COUNT_ACCESS,
  AppPermissions.SHOPPING_ACCESS,
  AppPermissions.USERS_ACCESS,
];

export function hasAnyPermission(
  userPermissions: string[] | null | undefined,
  required: AppPermissions[] | null | undefined,
): boolean {
  const userPerms = userPermissions ?? [];
  const req = required ?? [];
  if (req.length === 0) return true;
  return req.some((p) => userPerms.includes(p));
}

export function resolveLandingRoute(permisos: string[] | null | undefined): string {
  const userPerms = permisos ?? [];

  if (userPerms.includes(AppPermissions.ADMIN_ACCESS)) {
    return '/dashboard';
  }

  for (const perm of LANDING_PRIORITY) {
    if (userPerms.includes(perm)) {
      return DEFAULT_ROUTE_BY_PERMISSION[perm] ?? '/dashboard';
    }
  }

  return '/dashboard';
}
