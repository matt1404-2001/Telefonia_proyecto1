
-- ==========================================================
-- SISTEMA DE CONTROL DE LLAMADAS TELEFÓNICAS
-- Base de Datos: Telefonia
-- SQL Server
-- ==========================================================

IF DB_ID('Telefonia') IS NULL
BEGIN
    CREATE DATABASE Telefonia;
END
GO

USE Telefonia;
GO

-- ==========================================================
-- ELIMINAR OBJETOS SI EXISTEN (para pruebas y recreación)
-- ==========================================================

IF OBJECT_ID('dbo.vw_PrefijosConTarifa', 'V') IS NOT NULL
    DROP VIEW dbo.vw_PrefijosConTarifa;
GO

IF OBJECT_ID('dbo.Llamadas', 'U') IS NOT NULL
    DROP TABLE dbo.Llamadas;
GO

IF OBJECT_ID('dbo.CodigosPais', 'U') IS NOT NULL
    DROP TABLE dbo.CodigosPais;
GO

IF OBJECT_ID('dbo.Tarifas', 'U') IS NOT NULL
    DROP TABLE dbo.Tarifas;
GO

IF OBJECT_ID('dbo.Cuentas', 'U') IS NOT NULL
    DROP TABLE dbo.Cuentas;
GO

IF OBJECT_ID('dbo.GruposTarifarios', 'U') IS NOT NULL
    DROP TABLE dbo.GruposTarifarios;
GO

-- ==========================================================
-- TABLA: Cuentas
-- ==========================================================

CREATE TABLE Cuentas
(
    numero_telefono VARCHAR(15) PRIMARY KEY,

    tipo_servicio VARCHAR(10) NOT NULL
        CHECK (tipo_servicio IN ('PREPAGO', 'POSTPAGO')),

    saldo DECIMAL(14,2) NOT NULL
        CONSTRAINT DF_Cuentas_Saldo DEFAULT (0.00),

    proveedor VARCHAR(50) NOT NULL,

    bono_mismo_proveedor DECIMAL(14,2) NOT NULL
        CONSTRAINT DF_Cuentas_Bono DEFAULT (0.00),

    CONSTRAINT CK_Cuentas_Saldo_NoNegativo
        CHECK (saldo >= 0),

    CONSTRAINT CK_Cuentas_Bono_NoNegativo
        CHECK (bono_mismo_proveedor >= 0)
);
GO

-- ==========================================================
-- TABLA: GruposTarifarios
-- ==========================================================

CREATE TABLE GruposTarifarios
(
    id INT IDENTITY(1,1) PRIMARY KEY,

    nombre_grupo VARCHAR(10) NOT NULL
        UNIQUE,

    costo_por_min_usd DECIMAL(6,4) NOT NULL
);
GO

-- ==========================================================
-- TABLA: CodigosPais
-- ==========================================================

CREATE TABLE CodigosPais
(
    prefijo VARCHAR(6) PRIMARY KEY,

    nombre_pais VARCHAR(60) NOT NULL,

    id_grupo INT NOT NULL,

    CONSTRAINT FK_CodigosPais_Grupos
        FOREIGN KEY (id_grupo)
        REFERENCES GruposTarifarios(id)
);
GO

-- ==========================================================
-- TABLA: Tarifas
-- ==========================================================

CREATE TABLE Tarifas
(
    id INT IDENTITY(1,1) PRIMARY KEY,

    tipo_llamada INT NOT NULL,
    -- 1 = Mismo proveedor
    -- 2 = Otro proveedor
    -- 3 = Internacional

    destino_grupo VARCHAR(10) NOT NULL,

    costo_por_min DECIMAL(10,4) NOT NULL
);
GO

-- ==========================================================
-- TABLA: Llamadas
-- ==========================================================

CREATE TABLE Llamadas
(
    id INT IDENTITY(1,1) PRIMARY KEY,

    telefono_origen VARCHAR(15) NOT NULL,

    fecha_llamada INT NOT NULL,      -- YYYYMMDD

    hora_llamada INT NOT NULL,       -- HHMMSS

    telefono_destino VARCHAR(20) NOT NULL,

    costo_total BIGINT NOT NULL,

    duracion_segundos INT NOT NULL,

    fecha_registro DATETIME NOT NULL
        CONSTRAINT DF_Llamadas_FechaRegistro
        DEFAULT GETDATE(),

    CONSTRAINT FK_Llamadas_Cuentas
        FOREIGN KEY (telefono_origen)
        REFERENCES Cuentas(numero_telefono)
);
GO

-- ==========================================================
-- DATOS: Grupos Tarifarios
-- ==========================================================

INSERT INTO GruposTarifarios
(nombre_grupo, costo_por_min_usd)
VALUES
('C1',    0.14),
('B',     0.31),
('D',     0.54),
('CUBA',  1.03),
('RESTO', 1.03);
GO

-- ==========================================================
-- DATOS: Códigos de País
-- ==========================================================

-- Grupo C1
INSERT INTO CodigosPais VALUES ('501', 'Belice', 1);
INSERT INTO CodigosPais VALUES ('502', 'Guatemala', 1);
INSERT INTO CodigosPais VALUES ('503', 'El Salvador', 1);
INSERT INTO CodigosPais VALUES ('504', 'Honduras', 1);
INSERT INTO CodigosPais VALUES ('507', 'Panamá', 1);

-- Grupo B
INSERT INTO CodigosPais VALUES ('57', 'Colombia', 2);
INSERT INTO CodigosPais VALUES ('58', 'Venezuela', 2);
INSERT INTO CodigosPais VALUES ('55', 'Brasil', 2);
INSERT INTO CodigosPais VALUES ('56', 'Chile', 2);
INSERT INTO CodigosPais VALUES ('51', 'Perú', 2);
INSERT INTO CodigosPais VALUES ('54', 'Argentina', 2);
INSERT INTO CodigosPais VALUES ('593', 'Ecuador', 2);
INSERT INTO CodigosPais VALUES ('591', 'Bolivia', 2);
INSERT INTO CodigosPais VALUES ('595', 'Paraguay', 2);
INSERT INTO CodigosPais VALUES ('598', 'Uruguay', 2);
INSERT INTO CodigosPais VALUES ('1876', 'Jamaica', 2);
INSERT INTO CodigosPais VALUES ('1809', 'República Dom.', 2);
INSERT INTO CodigosPais VALUES ('1787', 'Puerto Rico', 2);

-- Grupo D
INSERT INTO CodigosPais VALUES ('44', 'Reino Unido', 3);
INSERT INTO CodigosPais VALUES ('49', 'Alemania', 3);
INSERT INTO CodigosPais VALUES ('33', 'Francia', 3);
INSERT INTO CodigosPais VALUES ('34', 'España', 3);
INSERT INTO CodigosPais VALUES ('39', 'Italia', 3);
INSERT INTO CodigosPais VALUES ('31', 'Países Bajos', 3);
INSERT INTO CodigosPais VALUES ('65', 'Singapur', 3);
INSERT INTO CodigosPais VALUES ('66', 'Tailandia', 3);
INSERT INTO CodigosPais VALUES ('61', 'Australia', 3);
INSERT INTO CodigosPais VALUES ('64', 'Nueva Zelanda', 3);

-- Cuba
INSERT INTO CodigosPais VALUES ('53', 'Cuba', 4);

-- Resto
INSERT INTO CodigosPais VALUES ('1', 'EE.UU./Canadá', 5);
INSERT INTO CodigosPais VALUES ('52', 'México', 5);
GO

-- ==========================================================
-- DATOS: Tarifas
-- ==========================================================

INSERT INTO Tarifas
(tipo_llamada, destino_grupo, costo_por_min)
VALUES
(1, 'fijo', 8.72),
(1, 'movil', 25.13),
(2, 'fijo', 8.72),
(2, 'movil', 25.13);

INSERT INTO Tarifas
(tipo_llamada, destino_grupo, costo_por_min)
VALUES
(3, 'C1', 0.14),
(3, 'B', 0.31),
(3, 'D', 0.54),
(3, 'CUBA', 1.03),
(3, 'RESTO', 1.03);
GO

-- ==========================================================
-- DATOS: Cuentas de prueba
-- ==========================================================

INSERT INTO Cuentas
(numero_telefono, tipo_servicio, saldo, proveedor, bono_mismo_proveedor)
VALUES
('25743715', 'PREPAGO', 5000.00, 'XYZ', 100.00),
('25262020', 'POSTPAGO', 0.00, 'XYZ', 0.00),
('88889999', 'PREPAGO', 1200.00, 'XYZ', 0.00),
('86001234', 'PREPAGO', 300.00, 'XYZ', 0.00),
('25001111', 'POSTPAGO', 0.00, 'XYZ', 0.00);
GO

-- ==========================================================
-- VISTA
-- ==========================================================

CREATE VIEW vw_PrefijosConTarifa
AS
SELECT
    cp.prefijo,
    cp.nombre_pais,
    gt.nombre_grupo,
    gt.costo_por_min_usd,
    t.costo_por_min AS costo_en_tarifas
FROM CodigosPais cp
INNER JOIN GruposTarifarios gt
    ON cp.id_grupo = gt.id
INNER JOIN Tarifas t
    ON t.destino_grupo = gt.nombre_grupo
   AND t.tipo_llamada = 3;
GO

-- ==========================================================
-- CONSULTAS DE VERIFICACIÓN
-- ==========================================================

SELECT * FROM Cuentas;
SELECT * FROM GruposTarifarios;
SELECT * FROM CodigosPais ORDER BY id_grupo, prefijo;
SELECT * FROM Tarifas ORDER BY tipo_llamada, destino_grupo;
SELECT * FROM vw_PrefijosConTarifa ORDER BY nombre_grupo;
GO

