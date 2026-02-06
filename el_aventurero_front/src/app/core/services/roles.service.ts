import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { ResponseModel } from '../../../shared/models/responde.models';
import { RolesListDto } from '../models/roles/list-roles.dto';

@Injectable({
  providedIn: 'root',
})
export class RolesService {
  private apiUrl = environment.rolesUrl;
  constructor(private http: HttpClient) {}

  getAtllRoles(): Observable<ResponseModel<RolesListDto[]>> {
    return this.http.get<ResponseModel<RolesListDto[]>>(`${this.apiUrl}/list`);
  }
}
