export interface CreateMesaDto {
  numero: number;
  estado: 'LIBRE' | 'OCUPADA';
  activo: number;
}
