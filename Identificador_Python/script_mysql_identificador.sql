CREATE DATABASE IF NOT EXISTS IdentificadorDB;
USE IdentificadorDB;

CREATE TABLE IF NOT EXISTS proveedor(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS telefono(
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL,
    proveedor_id INT NOT NULL,
    FOREIGN KEY(proveedor_id) REFERENCES proveedor(id)
);

CREATE TABLE IF NOT EXISTS tarjeta(
    id INT AUTO_INCREMENT PRIMARY KEY,
    telefono_id INT NOT NULL,
    identificador_telefono VARCHAR(255) NOT NULL,
    identificador_tarjeta VARCHAR(255) NOT NULL,
    FOREIGN KEY(telefono_id) REFERENCES telefono(id)
);

CREATE TABLE IF NOT EXISTS llamada(
    id INT AUTO_INCREMENT PRIMARY KEY,
    telefono_origen VARCHAR(20),
    telefono_destino VARCHAR(20),
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    estado VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS bitacora(
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME,
    transaccion VARCHAR(30),
    json_registro TEXT
);

INSERT INTO proveedor(nombre)
SELECT 'XYZ'
WHERE NOT EXISTS (SELECT 1 FROM proveedor WHERE nombre = 'XYZ');

INSERT INTO telefono(numero, activo, proveedor_id)
SELECT '88889999', TRUE, id
FROM proveedor
WHERE nombre = 'XYZ'
AND NOT EXISTS (SELECT 1 FROM telefono WHERE numero = '88889999');

INSERT INTO tarjeta(telefono_id, identificador_telefono, identificador_tarjeta)
SELECT id, '1234567890123456', '1234567890123456789'
FROM telefono
WHERE numero = '88889999'
AND NOT EXISTS (
    SELECT 1
    FROM tarjeta
    WHERE identificador_telefono = '1234567890123456'
);
