-- ============================================================
-- Migra las contraseñas de la tabla "usuarios" de texto plano a
-- su versión hasheada con SHA-256.
--
-- Es IDEMPOTENTE: si lo ejecutas más de una vez no pasa nada malo.
-- Un hash SHA-256 siempre mide 64 caracteres, así que este script
-- solo "hashea" las contraseñas que todavía NO midan 64 caracteres
-- (es decir, las que siguen en texto plano). Las que ya fueron
-- hasheadas antes se dejan intactas.
-- ============================================================

USE finanzas_personales;

-- MySQL Workbench activa por defecto el "Modo seguro", que bloquea
-- cualquier UPDATE/DELETE que no filtre por la columna llave (id).
-- Se desactiva solo para este script y se vuelve a activar al final.
SET SQL_SAFE_UPDATES = 0;

UPDATE usuarios
SET clave = SHA2(clave, 256)
WHERE LENGTH(clave) <> 64;

SET SQL_SAFE_UPDATES = 1;

-- Verificación: la columna "clave" debe verse como un texto largo
-- e ilegible (64 caracteres), no como la contraseña original.
SELECT id, usuario, clave, LENGTH(clave) AS largo_clave FROM usuarios;
