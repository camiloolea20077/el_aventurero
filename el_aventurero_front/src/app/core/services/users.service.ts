import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { IUsersFilterTable } from '../../modules/users/domain/models/users-filter-table.model';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { UsersTableModel } from '../../modules/users/domain/models/users-table.model';
import { CreateUsersDto } from '../../modules/users/domain/dto/create-users.dto';
import { ResponseModel } from '../../../shared/models/responde.models';
import { UsersModels } from '../../modules/users/domain/models/users.model';
import { UpdateUsersDto } from '../../modules/users/domain/dto/update-users.dto';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private apiUrl = environment.usersUrl;

  constructor(private http: HttpClient) {}

  pageClients(
    iFilterTable: IFilterTable<IUsersFilterTable>,
  ): Observable<ResponseTableModel<UsersTableModel>> {
    return this.http.post<ResponseTableModel<UsersTableModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }
  createUsers(
    employees: CreateUsersDto,
  ): Observable<ResponseModel<UsersModels>> {
    return this.http.post<ResponseModel<UsersModels>>(
      `${this.apiUrl}/create`,
      employees,
    );
  }
  updateUsers(
    id: number,
    employees: CreateUsersDto,
  ): Observable<ResponseModel<UpdateUsersDto>> {
    const updateEmployeesDto: UpdateUsersDto = { ...employees, id };
    return this.http.put<ResponseModel<UpdateUsersDto>>(
      `${this.apiUrl}/update`,
      updateEmployeesDto,
    );
  }
  deleteUsers(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }

  getUsersById(id: number): Observable<ResponseModel<UsersModels>> {
    return this.http.get<ResponseModel<UsersModels>>(`${this.apiUrl}/${id}`);
  }
}
