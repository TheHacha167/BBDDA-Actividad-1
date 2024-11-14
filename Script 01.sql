DROP DATABASE IF EXISTS Actividad_1_BBDDA;
CREATE DATABASE Actividad_1_BBDDA;
USE Actividad_1_BBDDA;

-- Tabla: Comunidad
CREATE TABLE Comunidad (
    comunidad_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    nombre VARCHAR(100) NOT NULL
);

-- Tabla: Provincia
CREATE TABLE Provincia (
    provincia_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    comunidad_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    FOREIGN KEY (comunidad_id) REFERENCES Comunidad(comunidad_id)
);

-- Tabla: Municipio
CREATE TABLE Municipio (
    municipio_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    provincia_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    FOREIGN KEY (provincia_id) REFERENCES Provincia(provincia_id)
);

-- Tabla: Localidad
CREATE TABLE Localidad (
    localidad_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    municipio_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    FOREIGN KEY (municipio_id) REFERENCES Municipio(municipio_id)
);

-- Tabla: Rotulo
CREATE TABLE Rotulo (
    rotulo_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    nombre VARCHAR(255) NOT NULL
);

-- Tabla: TipoEstacion
CREATE TABLE TipoEstacion (
    tipo_estacion_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    descripcion VARCHAR(50) NOT NULL
);

-- Tabla: TipoVenta
CREATE TABLE TipoVenta (
    tipo_venta_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    descripcion VARCHAR(50) NOT NULL
);

-- Tabla: Margen
CREATE TABLE Margen (
    margen_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    descripcion VARCHAR(50) NOT NULL
);

-- Tabla: EstacionServicio
CREATE TABLE EstacionServicio (
    estacion_id INT PRIMARY KEY,
    rotulo_id INT NOT NULL,
    localidad_id INT NOT NULL,
    tipo_estacion_id INT NOT NULL,
    codigo_postal VARCHAR(10),
    direccion VARCHAR(255),
    margen_id INT,
    latitud DECIMAL(10,6),
    longitud DECIMAL(11,6),
    toma_de_datos TIMESTAMP,  -- Para almacenar la fecha y hora juntas
    tipo_servicio TEXT, -- Para almacenar varios tipos de servicio ya que varchar 255 no es suficiente
    tipo_venta_id INT,
    rem VARCHAR(500),
    horario TEXT,
    FOREIGN KEY (rotulo_id) REFERENCES Rotulo(rotulo_id),
    FOREIGN KEY (localidad_id) REFERENCES Localidad(localidad_id),
    FOREIGN KEY (tipo_estacion_id) REFERENCES TipoEstacion(tipo_estacion_id),
    FOREIGN KEY (margen_id) REFERENCES Margen(margen_id),
    FOREIGN KEY (tipo_venta_id) REFERENCES TipoVenta(tipo_venta_id)
);


-- Tabla: Carburante
CREATE TABLE Carburante (
    carburante_id INT PRIMARY KEY,  -- Eliminado AUTO_INCREMENT
    nombre VARCHAR(100) NOT NULL
);

-- Tabla: PrecioCarburante
CREATE TABLE PrecioCarburante (
    estacion_id INT NOT NULL,
    carburante_id INT NOT NULL,
    precio DECIMAL(7,3) NOT NULL, -- (5,3) no era suficiente para almacenar precios como 1.123
    PRIMARY KEY (estacion_id, carburante_id),
    FOREIGN KEY (estacion_id) REFERENCES EstacionServicio(estacion_id),
    FOREIGN KEY (carburante_id) REFERENCES Carburante(carburante_id)
);


SELECT * FROM Comunidad;
SELECT p.provincia_id, p.nombre AS provincia, c.nombre AS comunidad
FROM Provincia p
JOIN Comunidad c ON p.comunidad_id = c.comunidad_id
WHERE c.nombre = 'Andalucía';

SELECT m.municipio_id, m.nombre AS municipio, p.nombre AS provincia
FROM Municipio m
JOIN Provincia p ON m.provincia_id = p.provincia_id
WHERE p.nombre = 'GRANADA';

SELECT m.municipio_id, m.nombre AS localidad, p.nombre AS provincia, c.nombre AS comunidad
FROM Municipio m
JOIN Provincia p ON m.provincia_id = p.provincia_id
JOIN Comunidad c ON p.comunidad_id = c.comunidad_id
WHERE p.nombre = 'Granada';

SELECT l.localidad_id, l.nombre AS localidad, m.nombre AS municipio, p.nombre AS provincia
FROM Localidad l
JOIN Municipio m ON l.municipio_id = m.municipio_id
JOIN Provincia p ON m.provincia_id = p.provincia_id
WHERE p.nombre = 'Granada';
