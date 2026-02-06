export class UsersModels {
  id: number;
  name: string;
  email: string;
  username: string;
  password: string;
  rol_id: number;
  active: number;
  permisos: string[];
  constructor(
    id: number,
    name: string,
    email: string,
    password: string,
    rol_id: number,
    farmId: number,
    active: number,
    username: string,
    permisos: string[] = [],
  ) {
    this.id = id;
    this.name = name;
    this.permisos = permisos;
    this.username = username;
    this.email = email;
    this.password = password;
    this.rol_id = rol_id;
    this.active = active;
  }
}
