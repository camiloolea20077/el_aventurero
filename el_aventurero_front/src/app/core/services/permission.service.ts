import { Injectable } from "@angular/core";
import { IndexDBService } from "./index-db.service";
import { AuthResponse } from "../../modules/mesas/interfaces/auth.model";
import { AppPermissions } from "../../../shared/interfaces/permissions.enum";


@Injectable({ providedIn: 'root' })
export class PermissionService {
  private permisos: string[] = [];

  constructor(private indexDBService: IndexDBService) {
    this.indexDBService.loadDataAuthDB().then((auth: AuthResponse | null) => {
      this.permisos = auth?.user.permisos || []
    });
  }

  isAdmin(): boolean {
    return this.permisos.includes(AppPermissions.ADMIN_ACCESS);
  }

  hasPermission(permission: AppPermissions): boolean {
    return this.permisos.includes(permission)
  }

  hasAny(permissions: AppPermissions[]): boolean {
    if (!permissions?.length) return true;
    if (this.isAdmin()) return true;
    return permissions.some((p) => this.permisos.includes(p));
  }

  getAll(): string[] {
    return this.permisos
  }

  setPermissions(permisos: string[]) {
    this.permisos = permisos ?? []
  }

}
