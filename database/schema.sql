-- Sistema de Procesamiento de Pagos - esquema MySQL (8.0.16 o superior por los CHECK)
-- Ejecutar completo en MySQL Workbench o con:  mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS sistema_pagos
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_pagos;

CREATE TABLE IF NOT EXISTS usuarios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- PBKDF2, nunca texto plano
    activo        TINYINT(1)   NOT NULL DEFAULT 1,
    creado_en     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ventas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(30)   NOT NULL UNIQUE,    -- código único visible
    descripcion VARCHAR(255)  NOT NULL,
    importe     DECIMAL(12,2) NOT NULL,
    fecha       DATE          NOT NULL,
    estado      ENUM('PENDIENTE','PAGADA') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT chk_ventas_importe CHECK (importe > 0),
    INDEX idx_ventas_fecha (fecha),
    INDEX idx_ventas_estado (estado)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transacciones (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    codigo           VARCHAR(30)   NOT NULL UNIQUE,
    venta_id         INT           NOT NULL,
    metodo           ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','PAGO_ELECTRONICO') NOT NULL,
    importe          DECIMAL(12,2) NOT NULL,
    fecha            DATETIME      NOT NULL,
    estado           ENUM('PENDIENTE','APROBADA','RECHAZADA') NOT NULL,
    motivo           VARCHAR(255)  NULL,          -- motivo del rechazo
    importe_recibido DECIMAL(12,2) NULL,          -- solo efectivo
    cambio           DECIMAL(12,2) NULL,          -- solo efectivo
    detalle          VARCHAR(255)  NULL,          -- datos seguros (tarjeta enmascarada, referencia...)
    CONSTRAINT fk_trans_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE RESTRICT,
    INDEX idx_trans_metodo (metodo),
    INDEX idx_trans_estado (estado),
    INDEX idx_trans_fecha (fecha),
    INDEX idx_trans_venta (venta_id)
) ENGINE=InnoDB;
