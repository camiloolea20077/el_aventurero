export interface UpdateProductoDto {
  id: number;
  nombre: string;
  tipo_venta?: string;
  tipo?: 'PRODUCTO' | 'INSUMO';
  categoria?: string;
  unidad_medida?: string;
  activo: number;
}
