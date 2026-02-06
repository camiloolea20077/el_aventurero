export interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  exact?: boolean;
  children?: MenuItem[];
  permissions?: import('./permissions.enum').AppPermissions[];
}
