import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ArqueoCajaModel } from '../models/arqueo-caja/arqueo-caja.model';
import { CreateArqueoDto } from '../models/arqueo-caja/create-arqueo.dto';
import { environment } from '../../../environments/environment';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
    providedIn: 'root',
})
export class ArqueoCajaService {
    private apiUrl = `${environment.apiUrl}/arqueo-caja`;

    constructor(private http: HttpClient) {}

    // Crear arqueo
    create(arqueo: CreateArqueoDto): Observable<ResponseModel<ArqueoCajaModel>> {
        return this.http.post<ResponseModel<ArqueoCajaModel>>(
            `${this.apiUrl}/create`,
            arqueo
        );
    }

    // Obtener arqueos por fecha
    getArqueosPorFecha(
        fechaInicio: string,
        fechaFin: string
    ): Observable<ResponseModel<ArqueoCajaModel[]>> {
        return this.http.get<ResponseModel<ArqueoCajaModel[]>>(
            `${this.apiUrl}/list?fecha_inicio=${fechaInicio}&fecha_fin=${fechaFin}`
        );
    }

    // Obtener arqueo por ID
    getArqueoById(id: number): Observable<ResponseModel<ArqueoCajaModel>> {
        return this.http.get<ResponseModel<ArqueoCajaModel>>(
            `${this.apiUrl}/${id}`
        );
    }

    // Obtener arqueo del día
    getArqueoDelDia(fecha: string): Observable<ResponseModel<ArqueoCajaModel>> {
        return this.http.get<ResponseModel<ArqueoCajaModel>>(
            `${this.apiUrl}/del-dia?fecha=${fecha}`
        );
    }

    // Obtener datos para arqueo (ingresos/egresos del día)
    getDatosParaArqueo(fecha: string): Observable<ResponseModel<any>> {
        return this.http.get<ResponseModel<any>>(
            `${this.apiUrl}/datos-dia?fecha=${fecha}`
        );
    }

    // Eliminar arqueo
    delete(id: number): Observable<ResponseModel<boolean>> {
        return this.http.delete<ResponseModel<boolean>>(
            `${this.apiUrl}/${id}`
        );
    }
}