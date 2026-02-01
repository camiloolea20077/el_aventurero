export interface CompraTableModel {
  id: number;
  total_compra: number;
  metodo_pago?: string;
  cantidad_productos: number;
  created_at: string;
  activo: number;
  total_rows?: number;
}
