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
