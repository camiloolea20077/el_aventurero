import { DetalleConteoModel } from './arqueo-caja.model';

export interface CreateArqueoDto {
    fecha: string;
    saldo_inicial: number;
    efectivo_real: number;
    observaciones?: string;
    detalle_conteo: DetalleConteoModel;
}