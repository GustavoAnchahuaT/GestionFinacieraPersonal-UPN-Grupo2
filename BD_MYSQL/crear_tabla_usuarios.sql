-- ============================================================
-- Ejecutar este script completo en MySQL Workbench (con el servidor
-- de MySQL de XAMPP corriendo), igual que hiciste con el script de
-- categorias y transacciones.
-- ============================================================

USE finanzas_personales;

-- ------------------------------------------------------------
-- Tabla: usuarios
-- Usuarios que pueden iniciar sesion en la app
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(100) NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO usuarios (usuario, clave) VALUES ('admin', 'admin123');
