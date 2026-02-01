import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { VentaModel } from '../models/venta/venta.model';
import { CreateVentaDto } from '../models/venta/create-venta.dto';
import { VentaTableModel } from '../models/venta/venta-table.model';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class VentaService {
  private apiUrl = environment.ventaUrl;

  constructor(private http: HttpClient) {}

  pageVenta(
    iFilterTable: IFilterTable<any>,
  ): Observable<ResponseTableModel<VentaTableModel>> {
    return this.http.post<ResponseTableModel<VentaTableModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }

  createVenta(
    createVenta: CreateVentaDto,
  ): Observable<ResponseModel<VentaModel>> {
    return this.http.post<ResponseModel<VentaModel>>(
      `${this.apiUrl}/create`,
      createVenta,
    );
  }

  getVentaById(id: number): Observable<ResponseModel<VentaModel>> {
    return this.http.get<ResponseModel<VentaModel>>(`${this.apiUrl}/${id}`);
  }

  deleteVenta(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }
}
