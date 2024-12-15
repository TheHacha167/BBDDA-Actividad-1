DROP DATABASE IF EXISTS Actividad_1_BBDDA;
CREATE DATABASE Actividad_1_BBDDA;
USE Actividad_1_BBDDA;

-- Tabla: Comunidad
-- Representa las comunidades autónomas
CREATE TABLE Comunidad (
    comunidad_id INT PRIMARY KEY,  -- ID único para cada comunidad
    nombre VARCHAR(100) NOT NULL  -- Nombre de la comunidad
);

-- Tabla: Provincia
-- Cada provincia pertenece a una comunidad
CREATE TABLE Provincia (
    provincia_id INT PRIMARY KEY,  -- ID único para cada provincia
    comunidad_id INT NOT NULL,  -- ID de la comunidad asociada (clave foránea)
    nombre VARCHAR(100) NOT NULL,  -- Nombre de la provincia
    FOREIGN KEY (comunidad_id) REFERENCES Comunidad(comunidad_id) -- Relación con la tabla Comunidad
);

-- Tabla: Municipio
-- Cada municipio pertenece a una provincia
CREATE TABLE Municipio (
    municipio_id INT PRIMARY KEY,  -- ID único para cada municipio
    provincia_id INT NOT NULL,  -- ID de la provincia asociada (clave foránea)
    nombre VARCHAR(100) NOT NULL,  -- Nombre del municipio
    FOREIGN KEY (provincia_id) REFERENCES Provincia(provincia_id) -- Relación con la tabla Provincia
);

-- Tabla: Localidad
-- Cada localidad pertenece a un municipio
CREATE TABLE Localidad (
    localidad_id INT PRIMARY KEY,  -- ID único para cada localidad
    municipio_id INT NOT NULL,  -- ID del municipio asociado (clave foránea)
    nombre VARCHAR(100) NOT NULL,  -- Nombre de la localidad
    FOREIGN KEY (municipio_id) REFERENCES Municipio(municipio_id) -- Relación con la tabla Municipio
);

-- Tabla: Rotulo
-- Representa los rótulos (marcas comerciales) de las estaciones de servicio
CREATE TABLE Rotulo (
    rotulo_id INT PRIMARY KEY,  -- ID único para cada rótulo
    nombre VARCHAR(255) NOT NULL -- Nombre del rótulo
);

-- Tabla: TipoEstacion
-- Representa el tipo de estación (terrestre o marítima)
CREATE TABLE TipoEstacion (
    tipo_estacion_id INT PRIMARY KEY,  -- ID único para cada tipo de estación
    descripcion VARCHAR(50) NOT NULL -- Descripción del tipo de estación
);

-- Tabla: TipoVenta
-- Representa los diferentes tipos de venta asociados a estaciones
CREATE TABLE TipoVenta (
    tipo_venta_id INT PRIMARY KEY,  -- ID único para cada tipo de venta
    descripcion VARCHAR(50) NOT NULL -- Descripción del tipo de venta
);

-- Tabla: Margen
-- Representa el margen respecto de la carretera de la estación de servicio
CREATE TABLE Margen (
    margen_id INT PRIMARY KEY,  -- ID único para cada margen
    descripcion VARCHAR(50) NOT NULL -- Descripción del margen
);

-- Tabla: EstacionServicio
-- Almacena la información de las estaciones de servicio
CREATE TABLE EstacionServicio (
    estacion_id INT PRIMARY KEY,  -- ID único para cada estación
    rotulo_id INT NOT NULL,  -- ID del rótulo asociado (clave foránea)
    localidad_id INT NOT NULL,  -- ID de la localidad asociada (clave foránea)
    tipo_estacion_id INT NOT NULL,  -- ID del tipo de estación (clave foránea)
    codigo_postal VARCHAR(10),  -- Código postal de la estación
    direccion VARCHAR(255),  -- Dirección física de la estación
    margen_id INT,  -- ID del margen asociado (clave foránea)
    latitud DECIMAL(10, 6),  -- Coordenada de latitud
    longitud DECIMAL(10, 6),  -- Coordenada de longitud
    toma_de_datos TIMESTAMP,  -- Fecha y hora de toma de datos
    tipo_servicio TEXT,  -- Información adicional
    tipo_venta_id INT,  -- ID del tipo de venta asociado (clave foránea)
    rem VARCHAR(500),  -- Información adicional
    horario TEXT,  -- Información adicional
    FOREIGN KEY (rotulo_id) REFERENCES Rotulo(rotulo_id), -- Relación con Rotulo
    FOREIGN KEY (localidad_id) REFERENCES Localidad(localidad_id), -- Relación con Localidad
    FOREIGN KEY (tipo_estacion_id) REFERENCES TipoEstacion(tipo_estacion_id), -- Relación con TipoEstacion
    FOREIGN KEY (margen_id) REFERENCES Margen(margen_id), -- Relación con Margen
    FOREIGN KEY (tipo_venta_id) REFERENCES TipoVenta(tipo_venta_id) -- Relación con TipoVenta
);

-- Tabla: Carburante
-- Representa los diferentes tipos de carburantes
CREATE TABLE Carburante (
    carburante_id INT PRIMARY KEY,  -- ID único para cada carburante
    nombre VARCHAR(100) NOT NULL -- Nombre del carburante 
);

-- Tabla: PrecioCarburante
-- Almacena los precios de los carburantes en las estaciones de servicio
CREATE TABLE PrecioCarburante (
    estacion_id INT NOT NULL,  -- ID de la estación asociada (clave foránea)
    carburante_id INT NOT NULL,  -- ID del carburante asociado (clave foránea)
    precio DECIMAL(7,3) NOT NULL,  -- Precio del carburante
    PRIMARY KEY (estacion_id, carburante_id),  -- Clave primaria compuesta por estacion_id y carburante_id
    FOREIGN KEY (estacion_id) REFERENCES EstacionServicio(estacion_id), -- Relación con EstacionServicio
    FOREIGN KEY (carburante_id) REFERENCES Carburante(carburante_id) -- Relación con Carburante
);

-- CONSULTAS

-- Empresa con más estaciones de servicio terrestres
-- Esta consulta busca el rótulo (empresa) que tiene más estaciones terrestres.
SELECT R.nombre AS Empresa, COUNT(E.estacion_id) AS TotalEstaciones
FROM EstacionServicio E
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN TipoEstacion T ON E.tipo_estacion_id = T.tipo_estacion_id
WHERE T.descripcion = 'Terrestre'  -- Filtra solo estaciones terrestres
GROUP BY R.nombre  -- Agrupa por empresa
ORDER BY TotalEstaciones DESC  -- Ordena por el número de estaciones en orden descendente
LIMIT 1;  -- Devuelve la empresa con más estaciones terrestres



-- Empresa con más estaciones de servicio marítimas
-- Similar a la consulta anterior, pero busca las estaciones marítimas.
SELECT R.nombre AS Empresa, COUNT(E.estacion_id) AS TotalEstaciones
FROM EstacionServicio E
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN TipoEstacion T ON E.tipo_estacion_id = T.tipo_estacion_id
WHERE T.descripcion = 'Marítima'  -- Filtra solo estaciones marítimas
GROUP BY R.nombre  -- Agrupa por empresa
ORDER BY TotalEstaciones DESC  -- Ordena por el número de estaciones en orden descendente
LIMIT 1;  -- Devuelve la empresa con más estaciones marítimas



-- Localización, empresa y margen de la estación con el precio más bajo para "Gasolina 95 E5" en la Comunidad de Madrid
-- Encuentra la estación con el precio más bajo de "Gasolina 95 E5" en Madrid.
SELECT
    L.nombre AS Localidad,  -- Nombre de la localidad
    R.nombre AS Empresa,  -- Nombre de la empresa
    M.descripcion AS Margen,  -- Margen de la estación
    PC.precio AS Precio  -- Precio del carburante
FROM PrecioCarburante PC
JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN Margen M ON E.margen_id = M.margen_id
JOIN Carburante CB ON PC.carburante_id = CB.carburante_id
JOIN Localidad L ON E.localidad_id = L.localidad_id
JOIN Municipio MU ON L.municipio_id = MU.municipio_id
JOIN Provincia P ON MU.provincia_id = P.provincia_id
JOIN Comunidad C ON P.comunidad_id = C.comunidad_id
WHERE CB.nombre = 'Precio gasolina 95 E5'  -- Filtra el carburante "Gasolina 95 E5"
  AND C.nombre LIKE 'Madrid'  -- Filtra la comunidad de Madrid
ORDER BY PC.precio ASC  -- Ordena por precio ascendente
LIMIT 1;  -- Devuelve solo la estación con el precio más bajo



-- Localización, empresa y margen de la estación con el precio más bajo para "Gasóleo A" cerca del centro de Albacete (radio de 10 km)
-- Encuentra la estación más cercana al centro de Albacete con el precio más bajo de "Gasóleo A".
SELECT
    L.nombre AS Localidad,  -- Nombre de la localidad
    R.nombre AS Empresa,  -- Nombre de la empresa
    M.descripcion AS Margen,  -- Margen de la estación
    PC.precio AS Precio,  -- Precio del carburante
    (6371000 * ACOS(  -- Calcula la distancia en metros entre dos puntos geográficos
        COS(RADIANS(38.995548)) * COS(RADIANS(E.latitud)) *
        COS(RADIANS(E.longitud) - RADIANS(-1.858542)) +
        SIN(RADIANS(38.995548)) * SIN(RADIANS(E.latitud))
    )) AS Distancia
FROM PrecioCarburante PC
JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN Localidad L ON E.localidad_id = L.localidad_id
JOIN Municipio MU ON L.municipio_id = MU.municipio_id
JOIN Provincia P ON MU.provincia_id = P.provincia_id
JOIN Margen M ON E.margen_id = M.margen_id
WHERE PC.carburante_id = 6  -- Filtra el carburante "Gasóleo A"
  AND P.nombre = 'Albacete'  -- Filtra la provincia de Albacete
HAVING Distancia <= 10000  -- Filtra estaciones en un radio de 10 km
ORDER BY PC.precio ASC  -- Ordena por precio ascendente
LIMIT 1;  -- Devuelve solo la estación más barata dentro del radio



-- Provincia de la estación marítima con el combustible "Gasolina 95 E5" más caro
-- Encuentra la provincia con el precio más alto de "Gasolina 95 E5" en estaciones marítimas.
SELECT
    P.nombre AS Provincia,  -- Nombre de la provincia
    MAX(PC.precio) AS PrecioMaximo  -- Precio más alto
FROM PrecioCarburante PC
JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
JOIN Localidad L ON E.localidad_id = L.localidad_id
JOIN Municipio M ON L.municipio_id = M.municipio_id
JOIN Provincia P ON M.provincia_id = P.provincia_id
JOIN TipoEstacion TE ON E.tipo_estacion_id = TE.tipo_estacion_id
WHERE TE.tipo_estacion_id = 2  -- Filtra solo estaciones marítimas
  AND PC.carburante_id = 1  -- Filtra el carburante "Gasolina 95 E5"
GROUP BY P.nombre  -- Agrupa por provincia
ORDER BY PrecioMaximo DESC  -- Ordena por precio descendente
LIMIT 1;  -- Devuelve la provincia con el precio más alto


-- ACTIVIDAD 2


SHOW INDEX FROM Provincia;

EXPLAIN SELECT ... FROM ... WHERE ...;


-- consulta 1
-- Empresa con más estaciones de servicio terrestres
-- Esta consulta busca el rótulo (empresa) que tiene más estaciones terrestres.
SELECT R.nombre AS Empresa, COUNT(E.estacion_id) AS TotalEstaciones
FROM EstacionServicio E
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN TipoEstacion T ON E.tipo_estacion_id = T.tipo_estacion_id
WHERE T.descripcion = 'Terrestre'  -- Filtra solo estaciones terrestres
GROUP BY R.nombre  -- Agrupa por empresa
    ORDER BY TotalEstaciones DESC  -- Ordena por el número de estaciones en orden descendente
    LIMIT 1;  -- Devuelve la empresa con más estaciones terrestres

EXPLAIN SELECT R.nombre AS Empresa, COUNT(E.estacion_id) AS TotalEstaciones
FROM EstacionServicio E
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN TipoEstacion T ON E.tipo_estacion_id = T.tipo_estacion_id
WHERE T.descripcion = 'Terrestre'
GROUP BY R.nombre
ORDER BY TotalEstaciones DESC
LIMIT 1;


CREATE INDEX idx_tipoestacion_descripcion ON TipoEstacion (descripcion);
CREATE INDEX idx_rotulo_nombre ON Rotulo (nombre);


-- consulta 2
    -- Empresa con más estaciones de servicio marítimas
    -- Similar a la consulta anterior, pero busca las estaciones marítimas.
    SELECT R.nombre AS Empresa, COUNT(E.estacion_id) AS TotalEstaciones
    FROM EstacionServicio E
    JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
    JOIN TipoEstacion T ON E.tipo_estacion_id = T.tipo_estacion_id
    WHERE T.descripcion = 'Marítima'  -- Filtra solo estaciones marítimas
    GROUP BY R.nombre  -- Agrupa por empresa
    ORDER BY TotalEstaciones DESC  -- Ordena por el número de estaciones en orden descendente
    LIMIT 1;  -- Devuelve la empresa con más estaciones marítimas

EXPLAIN SELECT R.nombre AS Empresa, COUNT(E.estacion_id) AS TotalEstaciones
FROM EstacionServicio E
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN TipoEstacion T ON E.tipo_estacion_id = T.tipo_estacion_id
WHERE T.descripcion = 'Marítima'
GROUP BY R.nombre
ORDER BY TotalEstaciones DESC
LIMIT 1;



-- consulta 3
    -- Localización, empresa y margen de la estación con el precio más bajo para "Gasolina 95 E5" en la Comunidad de Madrid
    -- Encuentra la estación con el precio más bajo de "Gasolina 95 E5" en Madrid.
    SELECT
        L.nombre AS Localidad,  -- Nombre de la localidad
        R.nombre AS Empresa,  -- Nombre de la empresa
        M.descripcion AS Margen,  -- Margen de la estación
        PC.precio AS Precio  -- Precio del carburante
    FROM PrecioCarburante PC
    JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
    JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
    JOIN Margen M ON E.margen_id = M.margen_id
    JOIN Carburante CB ON PC.carburante_id = CB.carburante_id
    JOIN Localidad L ON E.localidad_id = L.localidad_id
    JOIN Municipio MU ON L.municipio_id = MU.municipio_id
    JOIN Provincia P ON MU.provincia_id = P.provincia_id
    JOIN Comunidad C ON P.comunidad_id = C.comunidad_id
    WHERE CB.nombre = 'Precio gasolina 95 E5'  -- Filtra el carburante "Gasolina 95 E5"
      AND C.nombre LIKE 'Madrid'  -- Filtra la comunidad de Madrid
    ORDER BY PC.precio ASC  -- Ordena por precio ascendente
    LIMIT 1;  -- Devuelve solo la estación con el precio más bajo

EXPLAIN SELECT
    L.nombre AS Localidad,
    R.nombre AS Empresa,
    M.descripcion AS Margen,
    PC.precio AS Precio
FROM PrecioCarburante PC
JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
JOIN Margen M ON E.margen_id = M.margen_id
JOIN Carburante CB ON PC.carburante_id = CB.carburante_id
JOIN Localidad L ON E.localidad_id = L.localidad_id
JOIN Municipio MU ON L.municipio_id = MU.municipio_id
JOIN Provincia P ON MU.provincia_id = P.provincia_id
JOIN Comunidad C ON P.comunidad_id = C.comunidad_id
WHERE CB.nombre = 'Precio gasolina 95 E5'
  AND C.nombre LIKE 'Madrid'
ORDER BY PC.precio ASC
LIMIT 1;




CREATE INDEX idx_carburante_nombre ON Carburante (nombre);
CREATE INDEX idx_precio_carburante ON PrecioCarburante (precio, estacion_id);
CREATE INDEX idx_comunidad_nombre ON Comunidad (nombre);


CREATE INDEX idx_localidad_municipio ON Localidad (municipio_id);
CREATE INDEX idx_municipio_provincia ON Municipio (provincia_id);
CREATE INDEX idx_provincia_comunidad ON Provincia (comunidad_id);


SHOW INDEX FROM Carburante;
SHOW INDEX FROM Comunidad;
SHOW INDEX FROM PrecioCarburante;

-- consulta 4
    -- Localización, empresa y margen de la estación con el precio más bajo para "Gasóleo A" cerca del centro de Albacete (radio de 10 km)
    -- Encuentra la estación más cercana al centro de Albacete con el precio más bajo de "Gasóleo A".
    SELECT
        L.nombre AS Localidad,  -- Nombre de la localidad
        R.nombre AS Empresa,  -- Nombre de la empresa
        M.descripcion AS Margen,  -- Margen de la estación
        PC.precio AS Precio,  -- Precio del carburante
        (6371000 * ACOS(  -- Calcula la distancia en metros entre dos puntos geográficos
            COS(RADIANS(38.995548)) * COS(RADIANS(E.latitud)) *
            COS(RADIANS(E.longitud) - RADIANS(-1.858542)) +
            SIN(RADIANS(38.995548)) * SIN(RADIANS(E.latitud))
        )) AS Distancia
    FROM PrecioCarburante PC
    JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
    JOIN Rotulo R ON E.rotulo_id = R.rotulo_id
    JOIN Localidad L ON E.localidad_id = L.localidad_id
    JOIN Municipio MU ON L.municipio_id = MU.municipio_id
    JOIN Provincia P ON MU.provincia_id = P.provincia_id
    JOIN Margen M ON E.margen_id = M.margen_id
    WHERE PC.carburante_id = 6  -- Filtra el carburante "Gasóleo A"
      AND P.nombre = 'Albacete'  -- Filtra la provincia de Albacete
    HAVING Distancia <= 10000  -- Filtra estaciones en un radio de 10 km
    ORDER BY PC.precio ASC  -- Ordena por precio ascendente
    LIMIT 1;  -- Devuelve solo la estación más barata dentro del radio

CREATE INDEX idx_precio_carburante_id ON PrecioCarburante (carburante_id, precio);
CREATE INDEX idx_estacion_lat_lon ON EstacionServicio (latitud, longitud);


-- consulta 5
    -- Provincia de la estación marítima con el combustible "Gasolina 95 E5" más caro
    -- Encuentra la provincia con el precio más alto de "Gasolina 95 E5" en estaciones marítimas.
    SELECT
        P.nombre AS Provincia,  -- Nombre de la provincia
        MAX(PC.precio) AS PrecioMaximo  -- Precio más alto
    FROM PrecioCarburante PC
    JOIN EstacionServicio E ON PC.estacion_id = E.estacion_id
    JOIN Localidad L ON E.localidad_id = L.localidad_id
    JOIN Municipio M ON L.municipio_id = M.municipio_id
    JOIN Provincia P ON M.provincia_id = P.provincia_id
    JOIN TipoEstacion TE ON E.tipo_estacion_id = TE.tipo_estacion_id
    WHERE TE.tipo_estacion_id = 2  -- Filtra solo estaciones marítimas
      AND PC.carburante_id = 1  -- Filtra el carburante "Gasolina 95 E5"
    GROUP BY P.nombre  -- Agrupa por provincia
    ORDER BY PrecioMaximo DESC  -- Ordena por precio descendente
    LIMIT 1;  -- Devuelve la provincia con el precio más alto

CREATE INDEX idx_tipoestacion_id ON TipoEstacion (tipo_estacion_id);
