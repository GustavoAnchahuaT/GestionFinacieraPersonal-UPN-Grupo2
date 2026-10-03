-- ============================================================
-- Crea/ajusta la tabla "presupuestos" (ya existía en el script
-- original sin usarse). Guarda un monto único por mes/año.
-- Es seguro ejecutar esto varias veces, no falla si ya existe.
-- ============================================================

CREATE TABLE IF NOT EXISTS presupuestos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    anio INT NOT NULL,
    mes INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL
);

-- Agrega las columnas si la tabla ya existía con otra estructura
SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'presupuestos' AND COLUMN_NAME = 'anio'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE presupuestos ADD COLUMN anio INT NOT NULL DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'presupuestos' AND COLUMN_NAME = 'mes'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE presupuestos ADD COLUMN mes INT NOT NULL DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'presupuestos' AND COLUMN_NAME = 'monto'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE presupuestos ADD COLUMN monto DECIMAL(10,2) NOT NULL DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Un solo presupuesto por mes/año (evita duplicados)
SET @constraint_exists = (
    SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'presupuestos' AND CONSTRAINT_NAME = 'uk_anio_mes'
);
SET @sql = IF(@constraint_exists = 0, 'ALTER TABLE presupuestos ADD CONSTRAINT uk_anio_mes UNIQUE (anio, mes)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Verificación final: debe mostrar id, anio, mes, monto
DESCRIBE presupuestos;
