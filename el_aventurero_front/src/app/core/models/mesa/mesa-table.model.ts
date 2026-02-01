export interface MesaTableModel {
  id: number;
  numero: number;
  estado: 'LIBRE' | 'OCUPADA';
  total_acumulado: number;
  activo: number;
  total_rows?: number;
}
