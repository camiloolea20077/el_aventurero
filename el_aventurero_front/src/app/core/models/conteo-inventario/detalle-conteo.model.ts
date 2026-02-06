export interface DetalleConteoModel {
    id: number;
    conteo_id: number;
    producto_id: number;
    producto_nombre: string;
    stock_sistema: number;
    stock_fisico: number;
    diferencia: number;
    motivo?: string;
    ajustado: boolean;
    created_at?: string;
}