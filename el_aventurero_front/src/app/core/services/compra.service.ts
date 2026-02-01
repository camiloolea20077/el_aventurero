import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { CompraModel } from '../models/compra/compra.model';
import { CreateCompraDto } from '../models/compra/create-compra.dto';
import { CompraTableModel } from '../models/compra/compra-table.model';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class CompraService {
  private apiUrl = environment.compraUrl;

  constructor(private http: HttpClient) {}

  pageCompra(
    iFilterTable: IFilterTable<any>,
  ): Observable<ResponseTableModel<CompraTableModel>> {
    return this.http.post<ResponseTableModel<CompraTableModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }

  createCompra(
    createCompra: CreateCompraDto,
  ): Observable<ResponseModel<CompraModel>> {
    return this.http.post<ResponseModel<CompraModel>>(
      `${this.apiUrl}/create`,
      createCompra,
    );
  }

  getCompraById(id: number): Observable<ResponseModel<CompraModel>> {
    return this.http.get<ResponseModel<CompraModel>>(`${this.apiUrl}/${id}`);
  }

  deleteCompra(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }
}
