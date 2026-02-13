export interface ProductoModel {
  id: number;
  nombre: string;
  tipo_venta?: string; // UNIDAD o BOTELLA
  tipo: 'PRODUCTO' | 'INSUMO';
  categoria?: string;
  unidad_medida?: string;
  activo: number;
  created_at?: string;
  updated_at?: string;
}
