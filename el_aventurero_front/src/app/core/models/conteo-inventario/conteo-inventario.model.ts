export interface ConteoInventarioModel {
    id: number;
    fecha: string;
    tipo: 'PERIODICO' | 'CICLICO' | 'ANUAL';
    estado: 'EN_PROCESO' | 'COMPLETADO';
    created_at?: string;
    updated_at?: string;
}