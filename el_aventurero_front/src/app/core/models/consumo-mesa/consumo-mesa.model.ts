export interface ConsumoMesaModel {
  id: number;
  mesa_id: number;
  mesa_numero?: number;
  producto_id: number;
  producto_nombre?: string;
  tipo_venta?: string;
  cantidad: number;
  precio_unitario: number;
  subtotal: number;
  activo: number;
  created_at?: string;
  updated_at?: string;
}
