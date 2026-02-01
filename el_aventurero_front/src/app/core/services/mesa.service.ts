import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { MesaModel } from '../models/mesa/mesa.model';
import { CreateMesaDto } from '../models/mesa/create-mesa.dto';
import { UpdateMesaDto } from '../models/mesa/update-mesa.dto';
import { MesaTableModel } from '../models/mesa/mesa-table.model';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class MesaService {
  private apiUrl = environment.mesaUrl;

  constructor(private http: HttpClient) {}

  pageMesa(
    iFilterTable: IFilterTable<any>,
  ): Observable<ResponseTableModel<MesaTableModel>> {
    return this.http.post<ResponseTableModel<MesaTableModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }

  createMesa(createMesa: CreateMesaDto): Observable<ResponseModel<MesaModel>> {
    return this.http.post<ResponseModel<MesaModel>>(
      `${this.apiUrl}`,
      createMesa,
    );
  }

  updateMesa(updateMesa: UpdateMesaDto): Observable<ResponseModel<boolean>> {
    return this.http.put<ResponseModel<boolean>>(
      `${this.apiUrl}/update`,
      updateMesa,
    );
  }

  getMesaById(id: number): Observable<ResponseModel<MesaModel>> {
    return this.http.get<ResponseModel<MesaModel>>(`${this.apiUrl}/${id}`);
  }

  deleteMesa(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }

  getAllMesas(): Observable<ResponseModel<MesaModel[]>> {
    return this.http.get<ResponseModel<MesaModel[]>>(`${this.apiUrl}/list`);
  }

  getMesasByEstado(
    estado: 'LIBRE' | 'OCUPADA',
  ): Observable<ResponseModel<MesaModel[]>> {
    return this.http.get<ResponseModel<MesaModel[]>>(
      `${this.apiUrl}/estado/${estado}`,
    );
  }
}
