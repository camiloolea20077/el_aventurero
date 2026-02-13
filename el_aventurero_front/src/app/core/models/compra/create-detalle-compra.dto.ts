export interface CreateDetalleCompraDto {
  producto_id: number;
  cajas?: number;
  unidades_por_caja?: number;
  cantidad: number;
  costo_total: number;
  costo_unitario?: number;
  precio_sugerido?: number;
  precio_venta?: number;
  tipo?: string;
}
