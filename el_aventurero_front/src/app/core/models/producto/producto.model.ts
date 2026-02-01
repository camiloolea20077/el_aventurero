export interface ProductoModel {
  id: number;
  nombre: string;
  tipo_venta: string; // UNIDAD o BOTELLA
  activo: number;
  created_at?: string;
  updated_at?: string;
}
