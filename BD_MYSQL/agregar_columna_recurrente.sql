-- ============================================================
-- Ejecutar en MySQL Workbench (File -> Open SQL Script, o pega
-- y ejecuta en una pestaña nueva). Agrega el campo que usará el
-- nuevo CheckBox "Es un movimiento recurrente".
-- ============================================================

USE finanzas_personales;

ALTER TABLE transacciones
    ADD COLUMN es_recurrente TINYINT(1) NOT NULL DEFAULT 0
    AFTER descripcion;

-- Verificar que se agregó correctamente
DESCRIBE transacciones;
