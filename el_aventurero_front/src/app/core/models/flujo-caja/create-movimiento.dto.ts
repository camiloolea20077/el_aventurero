export interface CreateMovimientoDto {
  tipo: 'INGRESO' | 'EGRESO';
  concepto: string;
  categoria: string;
  monto: number;
  metodo_pago?: string;
  descripcion?: string;
  fecha: string;
}
