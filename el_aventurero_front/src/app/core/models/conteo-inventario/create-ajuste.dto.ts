export interface CreateAjusteDto {
    producto_id: number;
    tipo: 'SUMA' | 'RESTA';
    cantidad: number;
    motivo: string;
    descripcion: string;
    conteo_id?: number;
    fecha: string;
}