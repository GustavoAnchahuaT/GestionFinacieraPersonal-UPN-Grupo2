-- ============================================================
-- Ejecutar en MySQL Workbench (con MySQL de XAMPP corriendo).
-- Soluciona el bug de categorias duplicadas (ej. "Bono" x2).
-- Causa real: el script schema.sql inserta las 10 categorias
-- cada vez que se ejecuta, sin revisar si ya existian. Si lo
-- corriste mas de una vez, quedaron filas repetidas.
-- ============================================================

USE finanzas_personales;

-- ------------------------------------------------------------
-- PASO A: ver si hay duplicados (mismo nombre + tipo)
-- ------------------------------------------------------------
SELECT nombre, tipo, COUNT(*) AS veces
FROM categorias
GROUP BY nombre, tipo
HAVING COUNT(*) > 1;

-- Si esa consulta no devuelve filas, no habia duplicados en la
-- tabla y puedes saltar directo al PASO C.

-- ------------------------------------------------------------
-- PASO B: borrar los duplicados, dejando solo el id mas antiguo
-- de cada (nombre, tipo). OJO: si alguna transaccion ya quedo
-- registrada usando el id de una categoria "duplicada" que se
-- va a borrar, este paso fallara por la relacion (foreign key).
-- En ese caso avisame antes de seguir.
-- ------------------------------------------------------------
DELETE c1 FROM categorias c1
INNER JOIN categorias c2
    ON c1.nombre = c2.nombre
   AND c1.tipo = c2.tipo
   AND c1.id > c2.id;

-- ------------------------------------------------------------
-- PASO C: evitar que esto vuelva a pasar en el futuro
-- (si vuelves a correr schema.sql sin querer, ya no duplicara)
-- ------------------------------------------------------------
ALTER TABLE categorias
    ADD CONSTRAINT uk_categoria_nombre_tipo UNIQUE (nombre, tipo);

-- ------------------------------------------------------------
-- PASO D: verificar que quedaron 10 categorias, sin repetidos
-- ------------------------------------------------------------
SELECT id, nombre, tipo FROM categorias ORDER BY tipo, nombre;
