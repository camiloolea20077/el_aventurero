export interface UpdateInventarioDto {
  id: number;
  producto_id: number;
  stock: number;
  costo_unitario: number;
  precio_venta: number;
  activo: number;
}
