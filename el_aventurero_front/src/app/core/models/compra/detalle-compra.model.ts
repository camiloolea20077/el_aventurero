export interface DetalleCompraModel {
  id: number;
  compra_id: number;
  producto_id: number;
  producto_nombre?: string;
  cajas: number;
  unidades_por_caja: number;
  total_unidades: number;
  costo_total: number;
  costo_unitario: number;
  precio_sugerido?: number;
  precio_venta?: number;
  activo: number;
}
