import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { MovimientoCajaModel } from '../models/flujo-caja/movimiento-caja.model';
import { CreateMovimientoDto } from '../models/flujo-caja/create-movimiento.dto';
import { ResumenFlujoModel } from '../models/flujo-caja/resumen-flujo.model';
import { CierreSemanalModel } from '../models/flujo-caja/cierre-semanal.model';
import { environment } from '../../../environments/environment';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
  providedIn: 'root',
})
export class FlujoCajaService {
  private apiUrl = environment.cajaUrl;

  constructor(private http: HttpClient) {}

  // Registrar movimiento
  registrarMovimiento(
    movimiento: CreateMovimientoDto,
  ): Observable<ResponseModel<MovimientoCajaModel>> {
    return this.http.post<ResponseModel<MovimientoCajaModel>>(
      `${this.apiUrl}/movimiento`,
      movimiento,
    );
  }

  // Obtener movimientos por fecha
  getMovimientosPorFecha(
    fechaInicio: string,
    fechaFin: string,
  ): Observable<ResponseModel<MovimientoCajaModel[]>> {
    return this.http.get<ResponseModel<MovimientoCajaModel[]>>(
      `${this.apiUrl}/movimientos?fecha_inicio=${fechaInicio}&fecha_fin=${fechaFin}`,
    );
  }

  // Obtener resumen de flujo
  getResumenFlujo(
    fechaInicio: string,
    fechaFin: string,
  ): Observable<ResponseModel<ResumenFlujoModel>> {
    return this.http.get<ResponseModel<ResumenFlujoModel>>(
      `${this.apiUrl}/resumen?fecha_inicio=${fechaInicio}&fecha_fin=${fechaFin}`,
    );
  }

  // Obtener cierre semanal
  getCierreSemanal(
    fechaInicio: string,
    fechaFin: string,
  ): Observable<ResponseModel<CierreSemanalModel>> {
    return this.http.get<ResponseModel<CierreSemanalModel>>(
      `${this.apiUrl}/cierre-semanal?fecha_inicio=${fechaInicio}&fecha_fin=${fechaFin}`,
    );
  }

  // Eliminar movimiento
  deleteMovimiento(id: number): Observable<ResponseModel<boolean>> {
    return this.http.delete<ResponseModel<boolean>>(
      `${this.apiUrl}/movimiento/${id}`,
    );
  }
}
