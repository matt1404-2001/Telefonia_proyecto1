CREATE DATABASE IF NOT EXISTS IdentificadorDB;
USE IdentificadorDB;

INSERT INTO proveedor(nombre)
SELECT 'XYZ'
WHERE NOT EXISTS (SELECT 1 FROM proveedor WHERE nombre = 'XYZ');

INSERT INTO proveedor(nombre)
SELECT 'ABC'
WHERE NOT EXISTS (SELECT 1 FROM proveedor WHERE nombre = 'ABC');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '88889999', TRUE, id
FROM proveedor
WHERE nombre = 'XYZ'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '88889999');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '88001234', TRUE, id
FROM proveedor
WHERE nombre = 'XYZ'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '88001234');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '25743715', TRUE, id
FROM proveedor
WHERE nombre = 'XYZ'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '25743715');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '25262020', TRUE, id
FROM proveedor
WHERE nombre = 'XYZ'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '25262020');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '25001111', TRUE, id
FROM proveedor
WHERE nombre = 'ABC'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '25001111');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '87001111', TRUE, id
FROM proveedor
WHERE nombre = 'ABC'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '87001111');

INSERT INTO tarjeta(telefono_id, identificador_telefono, identificador_tarjeta)
SELECT id, '1234567890123456', '1234567890123456789'
FROM telefono
WHERE numero = '88889999'
AND NOT EXISTS (SELECT 1 FROM tarjeta WHERE identificador_telefono = '1234567890123456');

INSERT INTO tarjeta(telefono_id, identificador_telefono, identificador_tarjeta)
SELECT id, '2222222222222222', '2222222222222222222'
FROM telefono
WHERE numero = '25262020'
AND NOT EXISTS (SELECT 1 FROM tarjeta WHERE identificador_telefono = '2222222222222222');

INSERT INTO tarjeta(telefono_id, identificador_telefono, identificador_tarjeta)
SELECT id, '3333333333333333', '3333333333333333333'
FROM telefono
WHERE numero = '25743715'
AND NOT EXISTS (SELECT 1 FROM tarjeta WHERE identificador_telefono = '3333333333333333');

INSERT INTO tarjeta(telefono_id, identificador_telefono, identificador_tarjeta)
SELECT id, '4444444444444444', '4444444444444444444'
FROM telefono
WHERE numero = '25001111'
AND NOT EXISTS (SELECT 1 FROM tarjeta WHERE identificador_telefono = '4444444444444444');

SELECT p.nombre AS proveedor, t.numero, t.activo
FROM telefono t
JOIN proveedor p ON p.id = t.proveedor_id
ORDER BY p.nombre, t.numero;
