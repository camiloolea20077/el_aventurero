export interface VentaTableModel {
  id: number;
  mesa_id: number;
  mesa_numero: number;
  total: number;
  metodo_pago?: string;
  cantidad_productos: number;
  created_at: string;
  activo: number;
  total_rows?: number;
}
