export interface CreateDetalleCompraDto {
  producto_id: number;
  cajas: number;
  unidades_por_caja: number;
  costo_total: number;
  precio_sugerido?: number;
  precio_venta?: number;
}
