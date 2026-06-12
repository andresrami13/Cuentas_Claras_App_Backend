-- ============================================================
-- Migración: cifrado de columnas financieras sensibles
-- Proyecto: Cuentas Claras App
--
-- Los montos pasan de DECIMAL a VARCHAR y los textos se amplían, porque ahora
-- almacenan texto CIFRADO (AES-256-GCM) en lugar del valor en claro.
--
-- IMPORTANTE: ejecutar con datos de PRUEBA. Las filas existentes están en texto
-- plano y la aplicación, tras el cambio, intentará descifrarlas y fallará. Por eso
-- primero se VACÍAN las tablas afectadas. Si hubiera datos reales, NO ejecutar esto
-- sin un proceso de migración que cifre fila por fila.
-- ============================================================

-- 1) Vaciar datos de prueba (en orden seguro respecto a llaves foráneas)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE ai_coach_requests;
TRUNCATE TABLE financial_records;
TRUNCATE TABLE debts;
TRUNCATE TABLE financial_goals;
TRUNCATE TABLE budget_categories;
TRUNCATE TABLE fixed_budget_category;
SET FOREIGN_KEY_CHECKS = 1;

-- 2) financial_records
ALTER TABLE financial_records MODIFY description VARCHAR(1024) NULL;
ALTER TABLE financial_records MODIFY amount      VARCHAR(255)  NOT NULL;

-- 3) debts
ALTER TABLE debts MODIFY creditor       VARCHAR(1024) NOT NULL;
ALTER TABLE debts MODIFY description    VARCHAR(1024) NULL;
ALTER TABLE debts MODIFY initial_amount VARCHAR(255)  NOT NULL;
ALTER TABLE debts MODIFY pending_amount VARCHAR(255)  NOT NULL;

-- 4) financial_goals
ALTER TABLE financial_goals MODIFY name           VARCHAR(1024) NOT NULL;
ALTER TABLE financial_goals MODIFY description    VARCHAR(1024) NULL;
ALTER TABLE financial_goals MODIFY target_amount  VARCHAR(255)  NOT NULL;
ALTER TABLE financial_goals MODIFY current_amount VARCHAR(255)  NOT NULL;

-- 5) budget_categories
ALTER TABLE budget_categories MODIFY category_name   VARCHAR(1024) NOT NULL;
ALTER TABLE budget_categories MODIFY assigned_amount VARCHAR(255)  NOT NULL;
-- OPCIONAL: spent_amount ya no se usa (la aplicación lo calcula en memoria). Puede
-- dejarse tal cual; conserva su DEFAULT 0.00 y los INSERT siguen funcionando. Solo
-- elimínela si su motor lo permite (puede fallar si tiene índices/constraints asociados):
-- ALTER TABLE budget_categories DROP COLUMN spent_amount;

-- 6) fixed_budget_category
ALTER TABLE fixed_budget_category MODIFY category_name VARCHAR(1024) NOT NULL;
ALTER TABLE fixed_budget_category MODIFY amount        VARCHAR(255)  NOT NULL;

-- 7) ai_coach_requests
--    question pasa a guardar texto cifrado (cabe en 2048); ai_response ya es TEXT;
--    financial_context deja de guardarse en registros nuevos (se permite NULL).
ALTER TABLE ai_coach_requests MODIFY question          VARCHAR(2048) NOT NULL;
ALTER TABLE ai_coach_requests MODIFY financial_context TEXT          NULL;
