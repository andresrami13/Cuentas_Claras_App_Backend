-- ============================================================
-- Script de creación de tablas: Sistema de Ciclos de Presupuesto
-- Proyecto: Cuentas Claras App
-- ddl-auto: none — ejecutar manualmente en la base de datos
-- ============================================================

CREATE TABLE budget_cycles (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    user_document_number VARCHAR(12)   NOT NULL,
    start_date           DATE          NOT NULL,
    end_date             DATE          NOT NULL,
    payment_day          INT           NOT NULL,
    status               VARCHAR(20)   NOT NULL,
    created_at           DATETIME      NOT NULL,
    updated_at           DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_bc_user FOREIGN KEY (user_document_number)
        REFERENCES users (document_number)
);

-- category_name y assigned_amount almacenan texto CIFRADO (AES-256-GCM).
-- spent_amount no se almacena: la aplicación lo calcula sumando los gastos descifrados.
CREATE TABLE budget_categories (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    cycle_id        BIGINT          NOT NULL,
    category_name   VARCHAR(1024)   NOT NULL,
    assigned_amount VARCHAR(255)    NOT NULL,
    created_at      DATETIME        NOT NULL,
    updated_at      DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_bcat_cycle FOREIGN KEY (cycle_id)
        REFERENCES budget_cycles (id)
);

CREATE TABLE user_budget_config (
    id                   BIGINT      NOT NULL AUTO_INCREMENT,
    user_document_number VARCHAR(12) NOT NULL,
    payment_day          INT         NOT NULL,
    next_payment_date    DATE,
    created_at           DATETIME    NOT NULL,
    updated_at           DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT uq_ubc_user    UNIQUE (user_document_number),
    CONSTRAINT fk_ubc_user    FOREIGN KEY (user_document_number)
        REFERENCES users (document_number)
);

-- category_name y amount almacenan texto CIFRADO (AES-256-GCM).
CREATE TABLE fixed_budget_category (
    id                    BIGINT          NOT NULL AUTO_INCREMENT,
    user_budget_config_id BIGINT          NOT NULL,
    category_name         VARCHAR(1024)   NOT NULL,
    amount                VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_fbc_config FOREIGN KEY (user_budget_config_id)
        REFERENCES user_budget_config (id) ON DELETE CASCADE
);
