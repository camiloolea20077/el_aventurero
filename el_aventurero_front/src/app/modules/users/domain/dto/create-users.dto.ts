export interface CreateUsersDto {
  name: string;
  email: string;
  username: string;
  password: string;
  rol_id: number;
  active: number;
  permisos: string[];
}
