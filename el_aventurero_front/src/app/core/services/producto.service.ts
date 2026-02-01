import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProductoModel } from '../models/producto/producto.model';
import { CreateProductoDto } from '../models/producto/create-producto.dto';
import { UpdateProductoDto } from '../models/producto/update-producto.dto';
import { ProductoTableModel } from '../models/producto/producto-table.model';
import { ProductoListDto } from '../models/producto/producto-list.dto';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private apiUrl = environment.productoUrl;

  constructor(private http: HttpClient) {}

  pageProducto(
    iFilterTable: IFilterTable<any>,
  ): Observable<ResponseTableModel<ProductoTableModel>> {
    return this.http.post<ResponseTableModel<ProductoTableModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }

  createProducto(
    createProducto: CreateProductoDto,
  ): Observable<ResponseModel<ProductoModel>> {
    return this.http.post<ResponseModel<ProductoModel>>(
      `${this.apiUrl}/create`,
      createProducto,
    );
  }

  updateProducto(
    updateProducto: UpdateProductoDto,
  ): Observable<ResponseModel<boolean>> {
    return this.http.put<ResponseModel<boolean>>(
      `${this.apiUrl}/update`,
      updateProducto,
    );
  }

  getProductoById(id: number): Observable<ResponseModel<ProductoModel>> {
    return this.http.get<ResponseModel<ProductoModel>>(`${this.apiUrl}/${id}`);
  }

  deleteProducto(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }

  getAllProductos(): Observable<ResponseModel<ProductoListDto[]>> {
    return this.http.get<ResponseModel<ProductoListDto[]>>(
      `${this.apiUrl}/list`,
    );
  }
}
