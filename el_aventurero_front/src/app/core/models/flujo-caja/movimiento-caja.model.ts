export interface MovimientoCajaModel {
  id: number;
  tipo: 'INGRESO' | 'EGRESO';
  concepto: string;
  categoria: string;
  monto: number;
  metodo_pago?: string;
  venta_id?: number;
  compra_id?: number;
  descripcion?: string;
  fecha: string;
  created_at?: string;
  updated_at?: string;
}
