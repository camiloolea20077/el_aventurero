export interface ArqueoCajaModel {
  id: number;
  fecha: string;
  saldo_inicial: number;
  total_ingresos_sistema: number;
  total_egresos_sistema: number;
  saldo_esperado: number;
  efectivo_real: number;
  diferencia: number;
  estado: 'PENDIENTE' | 'CUADRADO' | 'AJUSTADO';
  observaciones?: string;

  // Detalle del conteo (campos individuales que vienen del backend)
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

  activo?: number;
  created_at?: string;
  updated_at?: string;
}

export interface DetalleConteoModel {
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
