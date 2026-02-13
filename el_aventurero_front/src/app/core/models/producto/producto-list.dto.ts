export interface ProductoListDto {
  id: number;
  nombre: string;
  tipo_venta?: 'UNIDAD' | 'BOTELLA';
  tipo: 'PRODUCTO' | 'INSUMO';
  categoria?: string;
  unidad_medida?: string;
  activo: number;
}
