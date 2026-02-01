import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InventarioModel } from '../models/inventario/inventario.model';
import { CreateInventarioDto } from '../models/inventario/create-inventario.dto';
import { UpdateInventarioDto } from '../models/inventario/update-inventario.dto';
import { InventarioTableModel } from '../models/inventario/inventario-table.model';
import { AjusteStockDto } from '../models/inventario/ajuste-stock.dto';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class InventarioService {
  private apiUrl = environment.inventarioUrl;

  constructor(private http: HttpClient) {}

  pageInventario(
    iFilterTable: IFilterTable<any>,
  ): Observable<ResponseTableModel<InventarioTableModel>> {
    return this.http.post<ResponseTableModel<InventarioTableModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }

  createInventario(
    createInventario: CreateInventarioDto,
  ): Observable<ResponseModel<InventarioModel>> {
    return this.http.post<ResponseModel<InventarioModel>>(
      `${this.apiUrl}/create`,
      createInventario,
    );
  }

  updateInventario(
    updateInventario: UpdateInventarioDto,
  ): Observable<ResponseModel<boolean>> {
    return this.http.put<ResponseModel<boolean>>(
      `${this.apiUrl}/update`,
      updateInventario,
    );
  }

  getInventarioById(id: number): Observable<ResponseModel<InventarioModel>> {
    return this.http.get<ResponseModel<InventarioModel>>(
      `${this.apiUrl}/${id}`,
    );
  }

  getInventarioByProductoId(
    productoId: number,
  ): Observable<ResponseModel<InventarioModel>> {
    return this.http.get<ResponseModel<InventarioModel>>(
      `${this.apiUrl}/producto/${productoId}`,
    );
  }

  deleteInventario(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }

  getAllInventario(): Observable<ResponseModel<InventarioModel[]>> {
    return this.http.get<ResponseModel<InventarioModel[]>>(
      `${this.apiUrl}/list`,
    );
  }

  ajustarStock(
    ajusteStock: AjusteStockDto,
  ): Observable<ResponseModel<boolean>> {
    return this.http.post<ResponseModel<boolean>>(
      `${this.apiUrl}/ajustar-stock`,
      ajusteStock,
    );
  }
}
