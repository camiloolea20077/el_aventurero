import { DetalleCompraModel } from './detalle-compra.model';

export interface CompraModel {
  id: number;
  total_compra: number;
  metodo_pago?: string;
  activo: number;
  created_at?: string;
  updated_at?: string;
  detalles?: DetalleCompraModel[];
}
