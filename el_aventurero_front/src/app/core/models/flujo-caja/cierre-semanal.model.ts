export interface CierreSemanalModel {
  semana: number;
  fecha_inicio: string;
  fecha_fin: string;
  ventas_totales: number;
  total_ingresos: number;
  total_egresos: number;
  balance_neto: number;
  cantidad_ventas: number;
  ticket_promedio: number;
  metodos_pago: MetodoPagoResumen[];
  productos_top: ProductoTopResumen[];
}

export interface MetodoPagoResumen {
  metodo: string;
  cantidad: number;
  total: number;
  porcentaje: number;
}

export interface ProductoTopResumen {
  producto_nombre: string;
  cantidad_vendida: number;
  total_vendido: number;
}
