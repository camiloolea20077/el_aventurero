export interface MesaModel {
  id: number;
  numero: number;
  estado: 'LIBRE' | 'OCUPADA';
  total_acumulado: number;
  activo: number;
  created_at?: string;
  updated_at?: string;
}
