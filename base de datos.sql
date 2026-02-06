CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INT NOT NULL REFERENCES roles(id),
    permisos text[] NOT NULL DEFAULT '{}'
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
select * from usuarios
ALTER TABLE usuarios ADD COLUMN permisos text[] NOT NULL DEFAULT '{}';
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL, -- ADMIN, CAJERO, MESERO
    descripcion VARCHAR(150),
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE mesas (
    id SERIAL PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    estado VARCHAR(10) NOT NULL CHECK (estado IN ('LIBRE','OCUPADA')),
    total_acumulado NUMERIC(12,2) DEFAULT 0,
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
            SELECT
                dc.id,
                dc.compra_id,
                dc.producto_id,
                p.nombre AS producto_nombre,
                dc.cajas,
                dc.unidades_por_caja,
                dc.total_unidades,
                dc.costo_total AS costo_total,
                dc.costo_unitario AS costo_unitario,
                dc.precio_sugerido AS precio_sugerido,
                dc.precio_venta AS precio_venta,
                dc.activo
            FROM detalle_compra dc
            INNER JOIN productos p ON p.id = 2
            WHERE dc.compra_id = 1
            AND dc.deleted_at IS NULL
            ORDER BY dc.id ASC
			
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo_venta VARCHAR(20) NOT NULL CHECK (tipo_venta IN ('UNIDAD','BOTELLA')),
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE TABLE inventario (
    id SERIAL PRIMARY KEY,
    producto_id INT NOT NULL REFERENCES productos(id),
    stock INT NOT NULL CHECK (stock >= 0),
    costo_unitario NUMERIC(12,2) NOT NULL,
    precio_venta NUMERIC(12,2) NOT NULL,
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

SELECT column_name
FROM information_schema.columns
WHERE table_name = 'productos'
ORDER BY column_name;
select * from mesas

CREATE TABLE compras (
    id SERIAL PRIMARY KEY,
    total_compra NUMERIC(12,2) NOT NULL,
    metodo_pago VARCHAR(20),
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE TABLE detalle_compra (
    id SERIAL PRIMARY KEY,
    compra_id INT NOT NULL REFERENCES compras(id),
    producto_id INT NOT NULL REFERENCES productos(id),
    cajas INT DEFAULT 0,
    unidades_por_caja INT DEFAULT 0,
    total_unidades INT NOT NULL,
    costo_total NUMERIC(12,2) NOT NULL,
    costo_unitario NUMERIC(12,2) NOT NULL,
    precio_sugerido NUMERIC(12,2),
    precio_venta NUMERIC(12,2),
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
            SELECT
                i.id,
                i.producto_id,
                p.nombre AS producto_nombre,
                p.tipo_venta,
                i.stock,
                i.costo_unitario AS costo_unitario,
                i.precio_venta AS precio_venta,
                i.activo,
                i.created_at,
                i.updated_at
            FROM inventario i
            INNER JOIN productos p ON p.id = i.producto_id
            WHERE i.producto_id = 2
            AND i.deleted_at IS NULL
            AND i.activo = 1
            LIMIT 1
			select * from usuarios
CREATE TABLE consumo_mesa (
    id SERIAL PRIMARY KEY,
    mesa_id INT NOT NULL REFERENCES mesas(id),
    producto_id INT NOT NULL REFERENCES productos(id),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(12,2) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE TABLE ventas (
    id SERIAL PRIMARY KEY,
    mesa_id INT NOT NULL REFERENCES mesas(id),
    total NUMERIC(12,2) NOT NULL,
    metodo_pago VARCHAR(20),
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE TABLE detalle_venta (
    id SERIAL PRIMARY KEY,
    venta_id INT NOT NULL REFERENCES ventas(id),
    producto_id INT NOT NULL REFERENCES productos(id),
    cantidad INT NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS movimiento_caja (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INGRESO', 'EGRESO')),
    concepto VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    monto DECIMAL(15, 2) NOT NULL CHECK (monto > 0),
    metodo_pago VARCHAR(50),
    descripcion TEXT,
    fecha DATE NOT NULL,
    venta_id BIGINT,
    compra_id BIGINT,
    activo BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE SET NULL,
    CONSTRAINT fk_compra FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE SET NULL
);

-- Crear índices para mejorar rendimiento
CREATE INDEX idx_movimiento_caja_fecha ON movimiento_caja(fecha);
CREATE INDEX idx_movimiento_caja_tipo ON movimiento_caja(tipo);
CREATE INDEX idx_movimiento_caja_categoria ON movimiento_caja(categoria);
CREATE INDEX idx_movimiento_caja_deleted_at ON movimiento_caja(deleted_at);

-- Crear tabla arqueo_caja
CREATE TABLE IF NOT EXISTS arqueo_caja (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    saldo_inicial DECIMAL(15, 2) NOT NULL,
    total_ingresos_sistema DECIMAL(15, 2) NOT NULL,
    total_egresos_sistema DECIMAL(15, 2) NOT NULL,
    saldo_esperado DECIMAL(15, 2) NOT NULL,
    efectivo_real DECIMAL(15, 2) NOT NULL,
    diferencia DECIMAL(15, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'CUADRADO', 'AJUSTADO')),
    observaciones TEXT,
    
    -- Detalle del conteo de billetes
    billetes_100000 INTEGER NOT NULL DEFAULT 0,
    billetes_50000 INTEGER NOT NULL DEFAULT 0,
    billetes_20000 INTEGER NOT NULL DEFAULT 0,
    billetes_10000 INTEGER NOT NULL DEFAULT 0,
    billetes_5000 INTEGER NOT NULL DEFAULT 0,
    billetes_2000 INTEGER NOT NULL DEFAULT 0,
    billetes_1000 INTEGER NOT NULL DEFAULT 0,
    
    -- Detalle del conteo de monedas
    monedas_1000 INTEGER NOT NULL DEFAULT 0,
    monedas_500 INTEGER NOT NULL DEFAULT 0,
    monedas_200 INTEGER NOT NULL DEFAULT 0,
    monedas_100 INTEGER NOT NULL DEFAULT 0,
    monedas_50 INTEGER NOT NULL DEFAULT 0,
    
    activo BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    
    -- Constraint para evitar duplicados por fecha
    CONSTRAINT uk_arqueo_caja_fecha UNIQUE (fecha, deleted_at)
);

-- Crear índices para mejorar rendimiento
CREATE INDEX idx_arqueo_caja_fecha ON arqueo_caja(fecha);
CREATE INDEX idx_arqueo_caja_estado ON arqueo_caja(estado);
CREATE INDEX idx_arqueo_caja_deleted_at ON arqueo_caja(deleted_at);

-- Comentarios de la tabla
COMMENT ON TABLE arqueo_caja IS 'Tabla para registrar arqueos diarios de caja';
COMMENT ON COLUMN arqueo_caja.estado IS 'Estado del arqueo: PENDIENTE (diferencia sin resolver), CUADRADO (sin diferencia), AJUSTADO (diferencia justificada)';
COMMENT ON COLUMN arqueo_caja.diferencia IS 'Diferencia entre efectivo real y saldo esperado (positivo = sobrante, negativo = faltante)';
COMMENT ON COLUMN arqueo_caja.billetes_100000 IS 'Cantidad de billetes de $100,000';
COMMENT ON COLUMN arqueo_caja.monedas_50 IS 'Cantidad de monedas de $50';

CREATE TABLE conteo_inventario (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo VARCHAR(20) DEFAULT 'PERIODICO', -- PERIODICO, CICLICO, ANUAL
    estado VARCHAR(20) DEFAULT 'EN_PROCESO', -- EN_PROCESO, COMPLETADO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE detalle_conteo (
    id BIGSERIAL PRIMARY KEY,
    conteo_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    stock_sistema INTEGER NOT NULL,
    stock_fisico INTEGER NOT NULL,
    diferencia INTEGER NOT NULL,
    motivo TEXT,
    ajustado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conteo_id) REFERENCES conteo_inventario(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE ajuste_inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- SUMA, RESTA
    cantidad INTEGER NOT NULL,
    motivo VARCHAR(100) NOT NULL, -- MERMA, ROBO, ERROR_CONTEO, VENTA_NO_REGISTRADA, OTRO
    descripcion TEXT,
    conteo_id BIGINT, -- Referencia opcional al conteo que originó el ajuste
    usuario_id BIGINT,
    fecha DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (conteo_id) REFERENCES conteo_inventario(id)
);

select * from usuarios

                                    SELECT
                                        o.id,
                                        o.nombre AS name,
                                        o.email AS email,
                                        r.nombre AS role,
                                        o.activo as activo,
                                        COUNT(*) OVER() AS total_rows
                                    FROM usuarios  o
                                    LEFT JOIN roles r ON r.id = o.rol_id
                                    WHERE
                                        o.deleted_at IS NULL
                                        AND o.activo = 1