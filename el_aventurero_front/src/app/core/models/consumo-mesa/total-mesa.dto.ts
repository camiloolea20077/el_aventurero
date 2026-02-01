export interface TotalMesaDto {
  mesa_id: number;
  mesa_numero: number;
  estado: 'LIBRE' | 'OCUPADA';
  total: number;
}
