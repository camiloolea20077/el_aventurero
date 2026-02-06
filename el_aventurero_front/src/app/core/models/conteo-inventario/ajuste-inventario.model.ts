export interface AjusteInventarioModel {
    id: number;
    producto_id: number;
    producto_nombre: string;
    tipo: 'SUMA' | 'RESTA';
    cantidad: number;
    motivo: string;
    descripcion?: string;
    conteo_id?: number;
    usuario_id?: number;
    fecha: string;
    created_at?: string;
}