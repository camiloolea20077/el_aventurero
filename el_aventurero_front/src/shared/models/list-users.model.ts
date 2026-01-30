export class ListUsersDto {
  id: number
  nombre_con_rol: string


  constructor(id: number, nombre_con_rol: string) {
    this.id = id
    this.nombre_con_rol = nombre_con_rol
  }
}
