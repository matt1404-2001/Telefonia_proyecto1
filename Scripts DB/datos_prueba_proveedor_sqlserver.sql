USE [Telefonia];
GO

IF COL_LENGTH('dbo.Cuentas', 'bono_mismo_proveedor') IS NULL
BEGIN
    ALTER TABLE dbo.Cuentas
    ADD bono_mismo_proveedor DECIMAL(14,2) NOT NULL
        CONSTRAINT DF_Cuentas_bono_mismo_proveedor DEFAULT (0.00);
END
GO

IF EXISTS (SELECT 1 FROM dbo.GruposTarifarios WHERE nombre_grupo = 'C1')
    UPDATE dbo.GruposTarifarios SET costo_por_min_usd = 0.1400 WHERE nombre_grupo = 'C1';
ELSE
    INSERT INTO dbo.GruposTarifarios(nombre_grupo, costo_por_min_usd) VALUES ('C1', 0.1400);

IF EXISTS (SELECT 1 FROM dbo.GruposTarifarios WHERE nombre_grupo = 'B')
    UPDATE dbo.GruposTarifarios SET costo_por_min_usd = 0.3100 WHERE nombre_grupo = 'B';
ELSE
    INSERT INTO dbo.GruposTarifarios(nombre_grupo, costo_por_min_usd) VALUES ('B', 0.3100);

IF EXISTS (SELECT 1 FROM dbo.GruposTarifarios WHERE nombre_grupo = 'D')
    UPDATE dbo.GruposTarifarios SET costo_por_min_usd = 0.5400 WHERE nombre_grupo = 'D';
ELSE
    INSERT INTO dbo.GruposTarifarios(nombre_grupo, costo_por_min_usd) VALUES ('D', 0.5400);

IF EXISTS (SELECT 1 FROM dbo.GruposTarifarios WHERE nombre_grupo = 'CUBA')
    UPDATE dbo.GruposTarifarios SET costo_por_min_usd = 1.0300 WHERE nombre_grupo = 'CUBA';
ELSE
    INSERT INTO dbo.GruposTarifarios(nombre_grupo, costo_por_min_usd) VALUES ('CUBA', 1.0300);

IF EXISTS (SELECT 1 FROM dbo.GruposTarifarios WHERE nombre_grupo = 'RESTO')
    UPDATE dbo.GruposTarifarios SET costo_por_min_usd = 1.0300 WHERE nombre_grupo = 'RESTO';
ELSE
    INSERT INTO dbo.GruposTarifarios(nombre_grupo, costo_por_min_usd) VALUES ('RESTO', 1.0300);
GO

IF EXISTS (SELECT 1 FROM dbo.CodigosPais WHERE prefijo = '502')
    UPDATE dbo.CodigosPais SET nombre_pais = 'Guatemala', id_grupo = (SELECT id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'C1') WHERE prefijo = '502';
ELSE
    INSERT INTO dbo.CodigosPais(prefijo, nombre_pais, id_grupo) SELECT '502', 'Guatemala', id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'C1';

IF EXISTS (SELECT 1 FROM dbo.CodigosPais WHERE prefijo = '57')
    UPDATE dbo.CodigosPais SET nombre_pais = 'Colombia', id_grupo = (SELECT id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'B') WHERE prefijo = '57';
ELSE
    INSERT INTO dbo.CodigosPais(prefijo, nombre_pais, id_grupo) SELECT '57', 'Colombia', id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'B';

IF EXISTS (SELECT 1 FROM dbo.CodigosPais WHERE prefijo = '49')
    UPDATE dbo.CodigosPais SET nombre_pais = 'Alemania', id_grupo = (SELECT id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'D') WHERE prefijo = '49';
ELSE
    INSERT INTO dbo.CodigosPais(prefijo, nombre_pais, id_grupo) SELECT '49', 'Alemania', id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'D';

IF EXISTS (SELECT 1 FROM dbo.CodigosPais WHERE prefijo = '53')
    UPDATE dbo.CodigosPais SET nombre_pais = 'Cuba', id_grupo = (SELECT id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'CUBA') WHERE prefijo = '53';
ELSE
    INSERT INTO dbo.CodigosPais(prefijo, nombre_pais, id_grupo) SELECT '53', 'Cuba', id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'CUBA';

IF EXISTS (SELECT 1 FROM dbo.CodigosPais WHERE prefijo = '1')
    UPDATE dbo.CodigosPais SET nombre_pais = 'Estados Unidos/Canada', id_grupo = (SELECT id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'RESTO') WHERE prefijo = '1';
ELSE
    INSERT INTO dbo.CodigosPais(prefijo, nombre_pais, id_grupo) SELECT '1', 'Estados Unidos/Canada', id FROM dbo.GruposTarifarios WHERE nombre_grupo = 'RESTO';
GO

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 1 AND destino_grupo = 'fijo')
    UPDATE dbo.Tarifas SET costo_por_min = 8.7200 WHERE tipo_llamada = 1 AND destino_grupo = 'fijo';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (1, 'fijo', 8.7200);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 1 AND destino_grupo = 'movil')
    UPDATE dbo.Tarifas SET costo_por_min = 25.1300 WHERE tipo_llamada = 1 AND destino_grupo = 'movil';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (1, 'movil', 25.1300);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 2 AND destino_grupo = 'fijo')
    UPDATE dbo.Tarifas SET costo_por_min = 8.7200 WHERE tipo_llamada = 2 AND destino_grupo = 'fijo';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (2, 'fijo', 8.7200);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 2 AND destino_grupo = 'movil')
    UPDATE dbo.Tarifas SET costo_por_min = 25.1300 WHERE tipo_llamada = 2 AND destino_grupo = 'movil';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (2, 'movil', 25.1300);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 3 AND destino_grupo = 'C1')
    UPDATE dbo.Tarifas SET costo_por_min = 0.1400 WHERE tipo_llamada = 3 AND destino_grupo = 'C1';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (3, 'C1', 0.1400);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 3 AND destino_grupo = 'B')
    UPDATE dbo.Tarifas SET costo_por_min = 0.3100 WHERE tipo_llamada = 3 AND destino_grupo = 'B';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (3, 'B', 0.3100);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 3 AND destino_grupo = 'D')
    UPDATE dbo.Tarifas SET costo_por_min = 0.5400 WHERE tipo_llamada = 3 AND destino_grupo = 'D';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (3, 'D', 0.5400);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 3 AND destino_grupo = 'CUBA')
    UPDATE dbo.Tarifas SET costo_por_min = 1.0300 WHERE tipo_llamada = 3 AND destino_grupo = 'CUBA';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (3, 'CUBA', 1.0300);

IF EXISTS (SELECT 1 FROM dbo.Tarifas WHERE tipo_llamada = 3 AND destino_grupo = 'RESTO')
    UPDATE dbo.Tarifas SET costo_por_min = 1.0300 WHERE tipo_llamada = 3 AND destino_grupo = 'RESTO';
ELSE
    INSERT INTO dbo.Tarifas(tipo_llamada, destino_grupo, costo_por_min) VALUES (3, 'RESTO', 1.0300);
GO

IF EXISTS (SELECT 1 FROM dbo.Cuentas WHERE numero_telefono = '88889999')
    UPDATE dbo.Cuentas SET tipo_servicio = 'PREPAGO', saldo = 1200.00, bono_mismo_proveedor = 100.00, proveedor = 'XYZ' WHERE numero_telefono = '88889999';
ELSE
    INSERT INTO dbo.Cuentas(numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor) VALUES ('88889999', 'PREPAGO', 1200.00, 100.00, 'XYZ');

IF EXISTS (SELECT 1 FROM dbo.Cuentas WHERE numero_telefono = '88001234')
    UPDATE dbo.Cuentas SET tipo_servicio = 'PREPAGO', saldo = 300.00, bono_mismo_proveedor = 0.00, proveedor = 'XYZ' WHERE numero_telefono = '88001234';
ELSE
    INSERT INTO dbo.Cuentas(numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor) VALUES ('88001234', 'PREPAGO', 300.00, 0.00, 'XYZ');

IF EXISTS (SELECT 1 FROM dbo.Cuentas WHERE numero_telefono = '25743715')
    UPDATE dbo.Cuentas SET tipo_servicio = 'PREPAGO', saldo = 5000.00, bono_mismo_proveedor = 50.00, proveedor = 'XYZ' WHERE numero_telefono = '25743715';
ELSE
    INSERT INTO dbo.Cuentas(numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor) VALUES ('25743715', 'PREPAGO', 5000.00, 50.00, 'XYZ');

IF EXISTS (SELECT 1 FROM dbo.Cuentas WHERE numero_telefono = '25262020')
    UPDATE dbo.Cuentas SET tipo_servicio = 'POSTPAGO', saldo = 0.00, bono_mismo_proveedor = 0.00, proveedor = 'XYZ' WHERE numero_telefono = '25262020';
ELSE
    INSERT INTO dbo.Cuentas(numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor) VALUES ('25262020', 'POSTPAGO', 0.00, 0.00, 'XYZ');

IF EXISTS (SELECT 1 FROM dbo.Cuentas WHERE numero_telefono = '25001111')
    UPDATE dbo.Cuentas SET tipo_servicio = 'POSTPAGO', saldo = 0.00, bono_mismo_proveedor = 0.00, proveedor = 'ABC' WHERE numero_telefono = '25001111';
ELSE
    INSERT INTO dbo.Cuentas(numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor) VALUES ('25001111', 'POSTPAGO', 0.00, 0.00, 'ABC');

IF EXISTS (SELECT 1 FROM dbo.Cuentas WHERE numero_telefono = '87001111')
    UPDATE dbo.Cuentas SET tipo_servicio = 'PREPAGO', saldo = 500.00, bono_mismo_proveedor = 0.00, proveedor = 'ABC' WHERE numero_telefono = '87001111';
ELSE
    INSERT INTO dbo.Cuentas(numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor) VALUES ('87001111', 'PREPAGO', 500.00, 0.00, 'ABC');
GO

SELECT numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor
FROM dbo.Cuentas
ORDER BY proveedor, numero_telefono;

SELECT tipo_llamada, destino_grupo, costo_por_min
FROM dbo.Tarifas
ORDER BY tipo_llamada, destino_grupo;
GO
