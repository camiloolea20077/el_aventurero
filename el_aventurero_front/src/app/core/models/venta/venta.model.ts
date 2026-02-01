import { DetalleVentaModel } from './detalle-venta.model';

export interface VentaModel {
  id: number;
  mesa_id: number;
  mesa_numero?: number;
  total: number;
  metodo_pago?: string;
  activo: number;
  created_at?: string;
  updated_at?: string;
  detalles?: DetalleVentaModel[];
}
