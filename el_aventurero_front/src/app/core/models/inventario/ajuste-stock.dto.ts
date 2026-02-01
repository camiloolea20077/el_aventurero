export interface AjusteStockDto {
  producto_id: number;
  cantidad: number;
  tipo: 'SUMA' | 'RESTA';
}
