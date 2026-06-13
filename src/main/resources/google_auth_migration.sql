-- ============================================================
-- Migración: inicio de sesión y registro con Google
-- Proyecto: Cuentas Claras App
--
-- Cambios sobre la tabla users. NO destructivo respecto a los datos existentes:
--   - password pasa a permitir NULL (cuentas que solo entran con Google).
--   - auth_provider distingue LOCAL (registro con contraseña) de GOOGLE.
--     Las filas existentes quedan como LOCAL por el DEFAULT.
--   - google_sub guarda el identificador estable del usuario en Google (claim "sub").
--   - índice único en email: la vinculación con Google es por correo, por lo que el
--     correo debe ser único.
--
-- IMPORTANTE: la creación del índice único FALLA si ya existen correos duplicados.
-- Antes de ejecutar, verificar que no haya duplicados:
--     SELECT email, COUNT(*) FROM users GROUP BY email HAVING COUNT(*) > 1;
-- ============================================================

ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NULL;

ALTER TABLE users ADD COLUMN auth_provider VARCHAR(10) NOT NULL DEFAULT 'LOCAL';

ALTER TABLE users ADD COLUMN google_sub VARCHAR(64) NULL;

ALTER TABLE users ADD UNIQUE INDEX uk_users_email (email);
