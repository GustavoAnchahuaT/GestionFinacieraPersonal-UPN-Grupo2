-- =====================================================================
--  schema.sql
--  Base de datos: finanzas_personales
--  Ejecutar este script completo en MySQL Workbench (con el servidor
--  de MySQL de XAMPP corriendo), o pegarlo en phpMyAdmin -> pestaña SQL.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS finanzas_personales
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE finanzas_personales;

-- ---------------------------------------------------------------------
-- Tabla: categorias
-- Catálogo de categorías (predefinidas + las que el usuario cree luego)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    tipo ENUM('ingreso', 'gasto', 'ambos') NOT NULL DEFAULT 'gasto',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- Tabla: transacciones
-- Un registro = un movimiento financiero (ingreso o gasto)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transacciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(10, 2) NOT NULL,
    tipo ENUM('ingreso', 'gasto') NOT NULL,
    categoria_id INT NOT NULL,
    descripcion VARCHAR(255) NULL,
    fecha DATE NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaccion_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_monto_positivo CHECK (monto > 0)
);

-- ---------------------------------------------------------------------
-- Tabla: presupuestos (opcional, para una futura fase del prototipo)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS presupuestos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mes INT NOT NULL,
    anio INT NOT NULL,
    monto_limite DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY uk_mes_anio (mes, anio)
);

-- ---------------------------------------------------------------------
-- Datos semilla: categorías básicas para poder probar la app de inmediato
-- ---------------------------------------------------------------------
INSERT INTO categorias (nombre, tipo) VALUES
    ('Alimentación', 'gasto'),
    ('Transporte', 'gasto'),
    ('Servicios (luz, agua, internet)', 'gasto'),
    ('Entretenimiento', 'gasto'),
    ('Salud', 'gasto'),
    ('Educación', 'gasto'),
    ('Otros gastos', 'gasto'),
    ('Sueldo', 'ingreso'),
    ('Bono / Extra', 'ingreso'),
    ('Otros ingresos', 'ingreso');

-- ---------------------------------------------------------------------
-- (Opcional) un par de transacciones de ejemplo para ver algo en el
-- dashboard apenas se conecte la app por primera vez
-- ---------------------------------------------------------------------
-- INSERT INTO transacciones (monto, tipo, categoria_id, descripcion, fecha)
-- VALUES
--     (1500.00, 'ingreso', 8, 'Sueldo del mes', CURDATE()),
--     (25.50, 'gasto', 1, 'Almuerzo', CURDATE()),
--     (60.00, 'gasto', 2, 'Pasajes de la semana', CURDATE());
