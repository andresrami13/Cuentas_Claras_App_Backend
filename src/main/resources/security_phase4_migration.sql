-- ============================================================
-- Migración: bloqueo automático de cuenta por intentos fallidos
-- Proyecto: Cuentas Claras App
--
-- Agrega dos columnas a la tabla users. Es NO destructivo: no borra datos.
-- Las filas existentes quedan con failed_login_attempts = 0 y locked_until = NULL.
-- ============================================================

ALTER TABLE users ADD COLUMN failed_login_attempts INT      NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN locked_until          DATETIME NULL;
