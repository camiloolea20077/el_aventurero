export interface InventarioTableModel {
  id: number;
  producto_id: number;
  producto_nombre: string;
  tipo_venta: string;
  stock: number;
  costo_unitario: number;
  precio_venta: number;
  valor_total: number;
  activo: number;
  total_rows?: number;
}
