import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ConsumoMesaModel } from '../models/consumo-mesa/consumo-mesa.model';
import { CreateConsumoMesaDto } from '../models/consumo-mesa/create-consumo-mesa.dto';
import { UpdateConsumoMesaDto } from '../models/consumo-mesa/update-consumo-mesa.dto';
import { TotalMesaDto } from '../models/consumo-mesa/total-mesa.dto';
import { environment } from '../../../environments/environment';
import { IFilterTable } from '../../../shared/models/filter-table';
import { ResponseTableModel } from '../../../shared/models/response-table.model';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class ConsumoMesaService {
  private apiUrl = environment.consumoMesaUrl;

  constructor(private http: HttpClient) {}

  pageConsumoMesa(
    iFilterTable: IFilterTable<any>,
  ): Observable<ResponseTableModel<ConsumoMesaModel>> {
    return this.http.post<ResponseTableModel<ConsumoMesaModel>>(
      `${this.apiUrl}/page`,
      iFilterTable,
    );
  }

  createConsumoMesa(
    createConsumo: CreateConsumoMesaDto,
  ): Observable<ResponseModel<ConsumoMesaModel>> {
    return this.http.post<ResponseModel<ConsumoMesaModel>>(
      `${this.apiUrl}/create`,
      createConsumo,
    );
  }

  updateConsumoMesa(
    updateConsumo: UpdateConsumoMesaDto,
  ): Observable<ResponseModel<boolean>> {
    return this.http.put<ResponseModel<boolean>>(
      `${this.apiUrl}/update`,
      updateConsumo,
    );
  }

  getConsumoMesaById(id: number): Observable<ResponseModel<ConsumoMesaModel>> {
    return this.http.get<ResponseModel<ConsumoMesaModel>>(
      `${this.apiUrl}/${id}`,
    );
  }

  getConsumosByMesaId(
    mesaId: number,
  ): Observable<ResponseModel<ConsumoMesaModel[]>> {
    return this.http.get<ResponseModel<ConsumoMesaModel[]>>(
      `${this.apiUrl}/mesa/${mesaId}`,
    );
  }

  getTotalByMesaId(mesaId: number): Observable<ResponseModel<TotalMesaDto>> {
    return this.http.get<ResponseModel<TotalMesaDto>>(
      `${this.apiUrl}/mesa/${mesaId}/total`,
    );
  }

  deleteConsumoMesa(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(`${this.apiUrl}/${id}`);
  }

  deleteConsumosByMesaId(mesaId: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(
      `${this.apiUrl}/mesa/${mesaId}`,
    );
  }
}
