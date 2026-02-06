import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ConteoInventarioModel } from '../models/conteo-inventario/conteo-inventario.model';
import { DetalleConteoModel } from '../models/conteo-inventario/detalle-conteo.model';
import { AjusteInventarioModel } from '../models/conteo-inventario/ajuste-inventario.model';
import { CreateConteoDto } from '../models/conteo-inventario/create-conteo.dto';
import { CreateDetalleConteoDto } from '../models/conteo-inventario/create-detalle-conteo.dto';
import { CreateAjusteDto } from '../models/conteo-inventario/create-ajuste.dto';
import { environment } from '../../../environments/environment';
import { ResponseModel } from '../../../shared/models/responde.models';

@Injectable({
    providedIn: 'root',
})
export class ConteoInventarioService {
    private apiUrl = environment.conteoInventarioUrl;

    constructor(private http: HttpClient) {}

    // ==================== CONTEO ====================

    iniciarConteo(conteo: CreateConteoDto): Observable<ResponseModel<ConteoInventarioModel>> {
        return this.http.post<ResponseModel<ConteoInventarioModel>>(
            `${this.apiUrl}/iniciar`,
            conteo
        );
    }

    completarConteo(conteoId: number): Observable<ResponseModel<ConteoInventarioModel>> {
        return this.http.put<ResponseModel<ConteoInventarioModel>>(
            `${this.apiUrl}/${conteoId}/completar`,
            {}
        );
    }

    getConteoById(id: number): Observable<ResponseModel<ConteoInventarioModel>> {
        return this.http.get<ResponseModel<ConteoInventarioModel>>(
            `${this.apiUrl}/${id}`
        );
    }

    getConteosPorFecha(
        fechaInicio: string,
        fechaFin: string
    ): Observable<ResponseModel<ConteoInventarioModel[]>> {
        return this.http.get<ResponseModel<ConteoInventarioModel[]>>(
            `${this.apiUrl}/list?fecha_inicio=${fechaInicio}&fecha_fin=${fechaFin}`
        );
    }

    getUltimoConteo(): Observable<ResponseModel<ConteoInventarioModel>> {
        return this.http.get<ResponseModel<ConteoInventarioModel>>(
            `${this.apiUrl}/ultimo`
        );
    }

    // ==================== DETALLES ====================

    registrarDetalle(detalle: CreateDetalleConteoDto): Observable<ResponseModel<DetalleConteoModel>> {
        return this.http.post<ResponseModel<DetalleConteoModel>>(
            `${this.apiUrl}/detalle`,
            detalle
        );
    }

    getDetallesByConteoId(conteoId: number): Observable<ResponseModel<DetalleConteoModel[]>> {
        return this.http.get<ResponseModel<DetalleConteoModel[]>>(
            `${this.apiUrl}/${conteoId}/detalles`
        );
    }

    getDiferencias(conteoId: number): Observable<ResponseModel<DetalleConteoModel[]>> {
        return this.http.get<ResponseModel<DetalleConteoModel[]>>(
            `${this.apiUrl}/${conteoId}/diferencias`
        );
    }

    getPendientesAjuste(conteoId: number): Observable<ResponseModel<DetalleConteoModel[]>> {
        return this.http.get<ResponseModel<DetalleConteoModel[]>>(
            `${this.apiUrl}/${conteoId}/pendientes`
        );
    }

    // ==================== AJUSTES ====================

    ajustarInventario(ajuste: CreateAjusteDto): Observable<ResponseModel<AjusteInventarioModel>> {
        return this.http.post<ResponseModel<AjusteInventarioModel>>(
            `${this.apiUrl}/ajustar`,
            ajuste
        );
    }

    getAjustesByConteoId(conteoId: number): Observable<ResponseModel<AjusteInventarioModel[]>> {
        return this.http.get<ResponseModel<AjusteInventarioModel[]>>(
            `${this.apiUrl}/${conteoId}/ajustes`
        );
    }

    getAjustesByProductoId(productoId: number): Observable<ResponseModel<AjusteInventarioModel[]>> {
        return this.http.get<ResponseModel<AjusteInventarioModel[]>>(
            `${this.apiUrl}/producto/${productoId}/ajustes`
        );
    }
}