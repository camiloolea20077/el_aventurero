export interface DetalleVentaModel {
  id: number;
  venta_id: number;
  producto_id: number;
  producto_nombre?: string;
  cantidad: number;
  precio_unitario: number;
  subtotal: number;
  activo: number;
}
