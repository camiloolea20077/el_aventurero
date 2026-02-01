export interface UpdateMesaDto {
  id: number;
  numero: number;
  estado: 'LIBRE' | 'OCUPADA';
  total_acumulado: number;
  activo: number;
}
