export interface Mesa {
  id?: number;
  numero: number;
  estado: 'LIBRE' | 'OCUPADA';
  total_acumulado: number;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
}

export interface Producto {
  id?: number;
  nombre: string;
  tipo_venta: 'UNIDAD' | 'BOTELLA';
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
}

export interface Inventario {
  id?: number;
  producto_id: number;
  producto?: Producto;
  stock: number;
  costo_unitario: number;
  precio_venta: number;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
}

export interface Compra {
  id?: number;
  total_compra: number;
  metodo_pago?: string;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
  detalles?: DetalleCompra[];
}

export interface DetalleCompra {
  id?: number;
  compra_id: number;
  producto_id: number;
  producto?: Producto;
  cajas: number;
  unidades_por_caja: number;
  total_unidades: number;
  costo_total: number;
  costo_unitario: number;
  precio_sugerido?: number;
  precio_venta?: number;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
}

export interface ConsumoMesa {
  id?: number;
  mesa_id: number;
  producto_id: number;
  producto?: Producto;
  cantidad: number;
  precio_unitario: number;
  subtotal: number;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
}

export interface Venta {
  id?: number;
  mesa_id: number;
  mesa?: Mesa;
  total: number;
  metodo_pago?: string;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
  detalles?: DetalleVenta[];
}

export interface DetalleVenta {
  id?: number;
  venta_id: number;
  producto_id: number;
  producto?: Producto;
  cantidad: number;
  precio_unitario: number;
  subtotal: number;
  activo: 1 | 2;
  created_at?: Date;
  updated_at?: Date;
  deleted_at?: Date;
}