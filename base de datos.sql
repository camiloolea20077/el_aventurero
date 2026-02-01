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
CREATE TABLE flujo_caja (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('INGRESO','EGRESO')),
    referencia VARCHAR(50),
    descripcion VARCHAR(150),
    monto NUMERIC(12,2) NOT NULL,
    activo INT DEFAULT 1 CHECK (activo IN (1,2)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

            SELECT 
                u.id AS id,
                u.email AS email,
                u.nombre_completo AS name,
                r.nombre AS role,
                array_to_json(u.permisos) AS permisos
            FROM usuarios u
            LEFT JOIN roles r ON r.id = u.role_id
            WHERE u.deleted_at IS NULL AND u.email = 'camiloolea200@gmail.com'
            LIMIT 1;
