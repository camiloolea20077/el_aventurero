import { CreateDetalleCompraDto } from './create-detalle-compra.dto';

export interface CreateCompraDto {
  metodo_pago?: string;
  detalles: CreateDetalleCompraDto[];
}
