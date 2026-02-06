export class UsersTableModel {
    id: number;
    name: string;
    email: string;
    role: string;
    activo: number;
    constructor(id: number, name: string, email: string, role: string, activo: number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.activo = activo;
    }
}