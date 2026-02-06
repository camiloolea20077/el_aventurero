import { DetalleConteoModel } from './arqueo-caja.model';

export interface CreateArqueoDto {
  fecha: string;
  saldo_inicial: number;
  efectivo_real: number;
  observaciones?: string;

  // Detalle del conteo (aplanado para el backend)
  billetes_100000: number;
  billetes_50000: number;
  billetes_20000: number;
  billetes_10000: number;
  billetes_5000: number;
  billetes_2000: number;
  billetes_1000: number;
  monedas_1000: number;
  monedas_500: number;
  monedas_200: number;
  monedas_100: number;
  monedas_50: number;
}
