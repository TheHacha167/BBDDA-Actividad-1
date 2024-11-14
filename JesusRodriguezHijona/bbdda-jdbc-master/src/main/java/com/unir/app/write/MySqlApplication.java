package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import com.unir.config.MySqlConnector;
import com.unir.model.MySqlCarburante;
import com.unir.model.MySqlComunidad;
import com.unir.model.MySqlLocalidad;
import com.unir.model.MySqlProvincia;
import com.unir.model.MySqlMunicipio;
import com.unir.model.MySqlPrecioCarburante;
import com.unir.model.MySqlMargen;
import com.unir.model.MySqlRotulo;
import com.unir.model.MySqlTipoEstacion;
import com.unir.model.MySqlTipoVenta;
import com.unir.model.MySqlEstacionServicio;

import lombok.extern.slf4j.Slf4j;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.time.format.DateTimeFormatter;


@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "Actividad_1_BBDDA";

    public static void main(String[] args) {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));


        // Establecemos la conexión a la base de datos
        try (Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero comunidad.csv debe estar en la raíz del proyecto, es decir, en la carpeta {}",
                    System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");
// /* 
            // Eliminar datos en el orden correcto para evitar conflictos de claves foráneas
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM PrecioCarburante");
            stmt.executeUpdate("DELETE FROM EstacionServicio");
            stmt.executeUpdate("DELETE FROM Localidad");
            stmt.executeUpdate("DELETE FROM Municipio");
            stmt.executeUpdate("DELETE FROM Provincia");
            stmt.executeUpdate("DELETE FROM Comunidad");
            stmt.executeUpdate("DELETE FROM Margen");
            stmt.executeUpdate("DELETE FROM Rotulo");
            stmt.executeUpdate("DELETE FROM TipoEstacion");
            stmt.executeUpdate("DELETE FROM TipoVenta");
            stmt.executeUpdate("DELETE FROM Carburante");
            log.info("Datos eliminados de todas las tablas.");
        } catch (SQLException e) {
            log.error("Error al borrar los datos de las tablas", e);
        }
// */

            // Leer datos del archivo CSV
            List<MySqlComunidad> comunidades = readComunidades();

            // Insertar datos en la base de datos
            insertDataComunidad(connection, comunidades);


            // Leer datos del archivo CSV
            List<MySqlProvincia> provincias = readProvincias();

            // Insertar datos en la base de datos
            insertDataProvinvia(connection, provincias);

            
            // Leer datos del archivo CSV
            List<MySqlMunicipio> municipios = readMunicipios();

            // Insertar datos en la base de datos
            insertDataMunicipio(connection, municipios);


            // Leer datos del archivo CSV
            List<MySqlLocalidad> localidades = readLocalidades();

            // Insertar datos en la base de datos
            insertDataLocalidad(connection, localidades);

            // Leer datos del archivo CSV
            List<MySqlMargen> margenes = readMargen();

            // Insertar datos en la base de datos
            insertDataMargen(connection, margenes);

            // Leer datos del archivo CSV
            List<MySqlRotulo> rotulos = readRotulo();

            // Insertar datos en la base de datos
            insertDataRotulo(connection, rotulos);

            // Leer datos del archivo CSV
            List<MySqlTipoEstacion> tipoEstaciones = readTipoEstacion();

            // Insertar datos en la base de datos
            insertDataTipoEstacion(connection, tipoEstaciones);

            // Leer datos del archivo CSV
            List<MySqlTipoVenta> tipoventas = readTipoVenta();

            // Insertar datos en la base de datos
            insertDataTipoVenta(connection, tipoventas);

            // Leer datos del archivo CSV
            List<MySqlCarburante> carburantes = readCarburante();

            // Insertar datos en la base de datos
            insertDataCarburante(connection, carburantes);
            
            // Leer datos del archivo CSV
            List<MySqlEstacionServicio> estacionesServicio = readEstacionServicio();

            // Insertar datos en la base de datos
            insertDataEstacionServicio(connection, estacionesServicio);


            // Leer datos del archivo CSV
            List<MySqlPrecioCarburante> preciosCarburante = readPrecioCarburante();

            // Insertar datos en la base de datos
            insertDataPrecioCarburante(connection, preciosCarburante);


        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }
   /*==========================================*
    *                                        *
    *                                        *
    *           COMUNIDADES                  *
    *                                        *
    *                                        *
    *                                        *
    ==========================================*/

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de comunidades.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de comunidades
     */
    private static List<MySqlComunidad> readComunidades() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Comnuidad.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build()) {

            List<MySqlComunidad> comunidades = new LinkedList<>();
            String[] nextLine;

            reader.skip(1); // Saltamos la primera línea del encabezado

            while ((nextLine = reader.readNext()) != null) {
                MySqlComunidad comunidad = new MySqlComunidad(
                        Integer.parseInt(nextLine[0]), // comunidad_id
                        nextLine[1]                    // nombre
                );
                comunidades.add(comunidad);
            }
            return comunidades;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Inserta los datos en la tabla Comunidad.
     * Si la comunidad ya existe, se actualiza; si no, se inserta.
     *
     * @param connection - Conexión a la base de datos
     * @param comunidades - Lista de comunidades a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void insertDataComunidad(Connection connection, List<MySqlComunidad> comunidades) throws SQLException {
        String insertSql = "INSERT INTO Comunidad (comunidad_id, nombre) VALUES (?, ?)";
        String updateSql = "UPDATE Comunidad SET nombre = ? WHERE comunidad_id = ?";

        // Lote para procesar las inserciones en batch
        int batchSize = 100;
        int count = 0;

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);

            for (MySqlComunidad comunidad : comunidades) {



                   // Insertar si no existe
                   insertStatement.setInt(1, comunidad.getComunidadId());
                   insertStatement.setString(2, comunidad.getNombre());
                   insertStatement.addBatch();

                    // Ejecutar lote cada batchSize registros
                    if (++count % batchSize == 0) {
                        updateStatement.executeBatch();
                        insertStatement.executeBatch();
                    }
                
            }

            // Ejecutar el batch final
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            connection.commit();
            log.info("Datos de Comunidad insertados y actualizados exitosamente.");

        } catch (SQLException e) {
            connection.rollback();
            log.error("Error al insertar o actualizar datos en la tabla Comunidad", e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
   /*==========================================*
    *                                        *
    *                                        *
    *           PROVINCIAS                   *
    *                                        *
    *                                        *
    *                                        *
    ==========================================*/

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de Provincias.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de Provincias
     */
    private static List<MySqlProvincia> readProvincias() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Provincia.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build()) {

            List<MySqlProvincia> provincias = new LinkedList<>();
            String[] nextLine;

            reader.skip(1); // Saltamos la primera línea del encabezado

            while ((nextLine = reader.readNext()) != null) {
                MySqlProvincia provincia = new MySqlProvincia(
                    Integer.parseInt(nextLine[0]), // provincia_id
                    Integer.parseInt(nextLine[1]), // comunidad_id
                    nextLine[2]                    // nombre
                );
                provincias.add(provincia);
            }
            return provincias;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }

 /**
     * Inserta los datos en la tabla Provincia.
     * Si la comunidad ya existe, se actualiza; si no, se inserta.
     *
     * @param connection - Conexión a la base de datos
     * @param provincias - Lista de provincias a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void insertDataProvinvia(Connection connection, List<MySqlProvincia> provincias) throws SQLException {
        String insertSql = "INSERT INTO Provincia (provincia_id,comunidad_id, nombre) VALUES (?,?,?)";
        String updateSql = "UPDATE Provincia SET nombre = ? WHERE provincia_id = ?";

        // Lote para procesar las inserciones en batch
        int batchSize = 100;// sé que no hay 100, pero no iba a poner 52 
        int count = 0;

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);

            for (MySqlProvincia provincia : provincias) {


                   // Insertar si no existe
                   insertStatement.setInt(1, provincia.getProvinciaId());
                   insertStatement.setInt(2, provincia.getComunidadId());
                   insertStatement.setString(3, provincia.getNombre());
                   insertStatement.addBatch();

                    // Ejecutar lote cada batchSize registros
                    if (++count % batchSize == 0) {
                        updateStatement.executeBatch();
                        insertStatement.executeBatch();
                    }
                
            }

            // Ejecutar el batch final
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            connection.commit();
            log.info("Datos de Provincias insertados y actualizados exitosamente.");

        } catch (SQLException e) {
            connection.rollback();
            log.error("Error al insertar o actualizar datos en la tabla Provincia", e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

   /*==========================================*
    *                                        *
    *                                        *
    *           MUNICIPIOS                   *
    *                                        *
    *                                        *
    *                                        *
    ==========================================*/
   
    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de Municipios.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de municipios
     */
    private static List<MySqlMunicipio> readMunicipios() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Municipio.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build()) {

            List<MySqlMunicipio> municipios = new LinkedList<>();
            String[] nextLine;

            reader.skip(1); // Saltamos la primera línea del encabezado

            while ((nextLine = reader.readNext()) != null) {
                MySqlMunicipio municipio = new MySqlMunicipio(
                    Integer.parseInt(nextLine[0]), // municipio_id
                    Integer.parseInt(nextLine[1]), // provincia_id
                    nextLine[2]                    // nombre
                );
                municipios.add(municipio);
            }
            return municipios;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }

 /**
     * Inserta los datos en la tabla Comunidad.
     * Si la comunidad ya existe, se actualiza; si no, se inserta.
     *
     * @param connection - Conexión a la base de datos
     * @param municipios - Lista de municipios a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void insertDataMunicipio(Connection connection, List<MySqlMunicipio> municipios) throws SQLException {
        String insertSql = "INSERT INTO Municipio (municipio_id,provincia_id, nombre) VALUES (?,?,?)";
        String updateSql = "UPDATE Municipio SET nombre = ? WHERE municipio_id = ?";

        // Lote para procesar las inserciones en batch
        int batchSize = 1500;
        int count = 0;

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);

            for (MySqlMunicipio municipio : municipios) {


                    // Insertar si no existe
                    insertStatement.setInt(1, municipio.getMunicipioId());
                    insertStatement.setInt(2, municipio.getProvinciaId());
                    insertStatement.setString(3, municipio.getNombre());
                    insertStatement.addBatch();

                    // Ejecutar lote cada batchSize registros
                    if (++count % batchSize == 0) {
                        updateStatement.executeBatch();
                        insertStatement.executeBatch();
                    }
                
            }

            // Ejecutar el batch final
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            connection.commit();
            log.info("Datos de Municipios insertados y actualizados exitosamente.");

        } catch (SQLException e) {
            connection.rollback();
            log.error("Error al insertar o actualizar datos en la tabla Municipio", e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

// 3 SEGUNDOS EN INSERTAR LO DE ARRIBA

   /*==========================================*
    *                                        *
    *                                        *
    *           LOCALIDADES                  *
    *                                        *
    *                                        *
    *                                        *
    ==========================================*/
   
    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de Localidades.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de localidades
     */
    private static List<MySqlLocalidad> readLocalidades() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Localidad.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build()) {

            List<MySqlLocalidad> localidades = new LinkedList<>();
            String[] nextLine;

            reader.skip(1); // Saltamos la primera línea del encabezado

            while ((nextLine = reader.readNext()) != null) {
                MySqlLocalidad localidad = new MySqlLocalidad(
                    Integer.parseInt(nextLine[0]), // localidad_id
                    Integer.parseInt(nextLine[1]), // municipio_id
                    nextLine[2]                    // nombre
                );
                localidades.add(localidad);
            }
            return localidades;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }

 /**
     * Inserta los datos en la tabla Localidad.
     * Si la localidad ya existe, se actualiza; si no, se inserta.
     *
     * @param connection - Conexión a la base de datos
     * @param municipios - Lista de localidades a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void insertDataLocalidad(Connection connection, List<MySqlLocalidad> localidades) throws SQLException {
        String insertSql = "INSERT INTO Localidad (localidad_id,municipio_id, nombre) VALUES (?,?,?)";
        String updateSql = "UPDATE Localidad SET nombre = ? WHERE localidad_id = ?";

        // Lote para procesar las inserciones en batch
        int batchSize = 1500;
        int count = 0;

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);

            for (MySqlLocalidad localidad : localidades) {


                    // Insertar si no existe
                    insertStatement.setInt(1, localidad.getLocalidadId());
                    insertStatement.setInt(2, localidad.getMunicipioId());
                    insertStatement.setString(3, localidad.getNombre());
                    insertStatement.addBatch();

                    // Ejecutar lote cada batchSize registros
                    if (++count % batchSize == 0) {
                        updateStatement.executeBatch();
                        insertStatement.executeBatch();
                    }
                
            }

            // Ejecutar el batch final
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            connection.commit();
            log.info("Datos de Localidades insertados y actualizados exitosamente.");

        } catch (SQLException e) {
            connection.rollback();
            log.error("Error al insertar o actualizar datos en la tabla Localidad", e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

   /*==========================================*
    *                                        *
    *                                        *
    *                Margen                  *
    *                                        *
    *                                        *
    *                                        *
    ==========================================*/

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de Márgenes.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de Márgen
     */
    private static List<MySqlMargen> readMargen() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Margen.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build()) {

            List<MySqlMargen> margenes = new LinkedList<>();
            String[] nextLine;

            reader.skip(1); // Saltamos la primera línea del encabezado

            while ((nextLine = reader.readNext()) != null) {
                MySqlMargen margen = new MySqlMargen(
                        Integer.parseInt(nextLine[0]), // margen_id
                        nextLine[1]                    // descripcion
                );
                margenes.add(margen);
            }
            return margenes;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Inserta los datos en la tabla Margen.
     * Si la comunidad ya existe, se actualiza; si no, se inserta.
     *
     * @param connection - Conexión a la base de datos
     * @param margenes - Lista de márgenes a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void insertDataMargen(Connection connection, List<MySqlMargen> margenes) throws SQLException {
        String insertSql = "INSERT INTO Margen (margen_id, descripcion) VALUES (?, ?)";
        String updateSql = "UPDATE Margen SET descripcion = ? WHERE margen_id = ?";

        // Lote para procesar las inserciones en batch
        int batchSize = 10;
        int count = 0;

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);

            for (MySqlMargen margen : margenes) {


                   // Insertar si no existe
                   insertStatement.setInt(1, margen.getMargenId());
                   insertStatement.setString(2, margen.getDescripcion());
                   insertStatement.addBatch();

                    // Ejecutar lote cada batchSize registros
                    if (++count % batchSize == 0) {
                        updateStatement.executeBatch();
                        insertStatement.executeBatch();
                    }
                
            }

            // Ejecutar el batch final
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            connection.commit();
            log.info("Datos de Margen insertados y actualizados exitosamente.");

        } catch (SQLException e) {
            connection.rollback();
            log.error("Error al insertar o actualizar datos en la tabla Margen", e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
 
   /*==========================================*
    *                                        *
    *                                        *
    *                Rotulos                 *
    *                                        *
    *                                        *
    *                                        *
    ==========================================*/

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de Rótulos.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de Rótulos
     */
    private static List<MySqlRotulo> readRotulo() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Rotulo.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build()) {

            List<MySqlRotulo> rotulos = new LinkedList<>();
            String[] nextLine;

            reader.skip(1); // Saltamos la primera línea del encabezado

            while ((nextLine = reader.readNext()) != null) {
                MySqlRotulo rotulo = new MySqlRotulo(
                        Integer.parseInt(nextLine[0]), // rotulo_id
                        nextLine[1]                    // nbombre
                );
                rotulos.add(rotulo);
            }
            return rotulos;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Inserta los datos en la tabla Rotulo.
     * Si la comunidad ya existe, se actualiza; si no, se inserta.
     *
     * @param connection - Conexión a la base de datos
     * @param rotulos - Lista de rotulos a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void insertDataRotulo(Connection connection, List<MySqlRotulo> rotulos) throws SQLException {
        String insertSql = "INSERT INTO Rotulo (rotulo_id, nombre) VALUES (?, ?)";
        String updateSql = "UPDATE Rotulo SET nombre = ? WHERE rotulo_id = ?";

        // Lote para procesar las inserciones en batch
        int batchSize = 1500;
        int count = 0;

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);

            for (MySqlRotulo rotulo : rotulos) {


                   // Insertar si no existe
                   insertStatement.setInt(1, rotulo.getRotuloId());
                   insertStatement.setString(2, rotulo.getNombre());
                   insertStatement.addBatch();

                    // Ejecutar lote cada batchSize registros
                    if (++count % batchSize == 0) {
                        updateStatement.executeBatch();
                        insertStatement.executeBatch();
                    }
                
            }

            // Ejecutar el batch final
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            connection.commit();
            log.info("Datos de Rotulo insertados y actualizados exitosamente.");

        } catch (SQLException e) {
            connection.rollback();
            log.error("Error al insertar o actualizar datos en la tabla Rotulo", e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
 
/*==========================================*
 *                                          *
 *               Tipo Estacion              *
 *                                          *
 *==========================================*/

/**
 * Lee los datos del fichero CSV y los devuelve en una lista de Tipos de Estación.
 * El fichero CSV debe estar en la raíz del proyecto.
 *
 * @return - Lista de Tipos de Estación
 */
private static List<MySqlTipoEstacion> readTipoEstacion() {

    try (CSVReader reader = new CSVReaderBuilder(
            new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\TipoEstacion.csv"))
            .withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build())
            .build()) {

        List<MySqlTipoEstacion> tiposEstacion = new LinkedList<>();
        String[] nextLine;

        reader.skip(1); // Saltamos la primera línea del encabezado

        while ((nextLine = reader.readNext()) != null) {
            MySqlTipoEstacion tipoEstacion = new MySqlTipoEstacion(
                    Integer.parseInt(nextLine[0]), // tipo_estacion_Id
                    nextLine[1]                    // descripcion
            );
            tiposEstacion.add(tipoEstacion);
        }
        return tiposEstacion;
    } catch (IOException | CsvValidationException e) {
        log.error("Error al leer el fichero CSV", e);
        throw new RuntimeException(e);
    }
}

/**
 * Inserta los datos en la tabla TipoEstacion.
 * Si el tipo de estación ya existe, se actualiza; si no, se inserta.
 *
 * @param connection - Conexión a la base de datos
 * @param tiposEstacion - Lista de tipos de estación a insertar o actualizar
 * @throws SQLException - Error al ejecutar la consulta
 */
private static void insertDataTipoEstacion(Connection connection, List<MySqlTipoEstacion> tiposEstacion) throws SQLException {
    String insertSql = "INSERT INTO TipoEstacion (tipo_estacion_id, descripcion) VALUES (?, ?)";
    String updateSql = "UPDATE TipoEstacion SET descripcion = ? WHERE tipo_estacion_id = ?";

    // Lote para procesar las inserciones en batch
    int batchSize = 10;
    int count = 0;

    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
         PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

        connection.setAutoCommit(false);

        for (MySqlTipoEstacion tipoEstacion : tiposEstacion) {
            // Insertar si no existe
            insertStatement.setInt(1, tipoEstacion.getTipo_estacion_Id());
            insertStatement.setString(2, tipoEstacion.getDescripcion());
            insertStatement.addBatch();

            // Ejecutar lote cada batchSize registros
            if (++count % batchSize == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutar el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        connection.commit();
        log.info("Datos de TipoEstacion insertados y actualizados exitosamente.");

    } catch (SQLException e) {
        connection.rollback();
        log.error("Error al insertar o actualizar datos en la tabla TipoEstacion", e);
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}


/*==========================================*
 *                                          *
 *               Tipo Venta                 *
 *                                          *
 *==========================================*/

/**
 * Lee los datos del fichero CSV y los devuelve en una lista de Tipos de Venta.
 * El fichero CSV debe estar en la raíz del proyecto.
 *
 * @return - Lista de Tipos de Venta
 */
private static List<MySqlTipoVenta> readTipoVenta() {

    try (CSVReader reader = new CSVReaderBuilder(
            new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\TipoVenta.csv"))
            .withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build())
            .build()) {

        List<MySqlTipoVenta> tiposVenta = new LinkedList<>();
        String[] nextLine;

        reader.skip(1); // Saltamos la primera línea del encabezado

        while ((nextLine = reader.readNext()) != null) {
            MySqlTipoVenta tipoVenta = new MySqlTipoVenta(
                    Integer.parseInt(nextLine[0]), // tipo_venta_Id
                    nextLine[1]                    // descripcion
            );
            tiposVenta.add(tipoVenta);
        }
        return tiposVenta;
    } catch (IOException | CsvValidationException e) {
        log.error("Error al leer el fichero CSV", e);
        throw new RuntimeException(e);
    }
}

/**
 * Inserta los datos en la tabla TipoVenta.
 * Si el tipo de venta ya existe, se actualiza; si no, se inserta.
 *
 * @param connection - Conexión a la base de datos
 * @param tiposVenta - Lista de tipos de venta a insertar o actualizar
 * @throws SQLException - Error al ejecutar la consulta
 */
private static void insertDataTipoVenta(Connection connection, List<MySqlTipoVenta> tiposVenta) throws SQLException {
    String insertSql = "INSERT INTO TipoVenta (tipo_venta_id, descripcion) VALUES (?, ?)";
    String updateSql = "UPDATE TipoVenta SET descripcion = ? WHERE tipo_venta_id = ?";

    // Lote para procesar las inserciones en batch
    int batchSize = 10;
    int count = 0;

    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
         PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

        connection.setAutoCommit(false);

        for (MySqlTipoVenta tipoVenta : tiposVenta) {
            // Insertar si no existe
            insertStatement.setInt(1, tipoVenta.getTipo_venta_Id());
            insertStatement.setString(2, tipoVenta.getDescripcion());
            insertStatement.addBatch();

            // Ejecutar lote cada batchSize registros
            if (++count % batchSize == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutar el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        connection.commit();
        log.info("Datos de TipoVenta insertados y actualizados exitosamente.");

    } catch (SQLException e) {
        connection.rollback();
        log.error("Error al insertar o actualizar datos en la tabla TipoVenta", e);
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}


/*==========================================*
 *                                          *
 *               Carburante                 *
 *                                          *
 *==========================================*/

/**
 * Lee los datos del fichero CSV y los devuelve en una lista de Carburantes.
 * El fichero CSV debe estar en la raíz del proyecto.
 *
 * @return - Lista de Carburantes
 */
private static List<MySqlCarburante> readCarburante() {

    try (CSVReader reader = new CSVReaderBuilder(
            new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\Carburante.csv"))
            .withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build())
            .build()) {

        List<MySqlCarburante> carburantes = new LinkedList<>();
        String[] nextLine;

        reader.skip(1); // Saltamos la primera línea del encabezado

        while ((nextLine = reader.readNext()) != null) {
            MySqlCarburante carburante = new MySqlCarburante(
                    Integer.parseInt(nextLine[0]), // tcarburante_Id
                    nextLine[1]                    // nombre
            );
            carburantes.add(carburante);
        }
        return carburantes;
    } catch (IOException | CsvValidationException e) {
        log.error("Error al leer el fichero CSV", e);
        throw new RuntimeException(e);
    }
}

/**
 * Inserta los datos en la tabla Carburante.
 * Si el carburante ya existe, se actualiza; si no, se inserta.
 *
 * @param connection - Conexión a la base de datos
 * @param carburantes - Lista de carburantes a insertar o actualizar
 * @throws SQLException - Error al ejecutar la consulta
 */
private static void insertDataCarburante(Connection connection, List<MySqlCarburante> carburantes) throws SQLException {
    String insertSql = "INSERT INTO Carburante (carburante_id, nombre) VALUES (?, ?)";
    String updateSql = "UPDATE Carburante SET nombre = ? WHERE tcarburante_id = ?";

    // Lote para procesar las inserciones en batch
    int batchSize = 1500;
    int count = 0;

    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
         PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

        connection.setAutoCommit(false);

        for (MySqlCarburante carburante : carburantes) {
            // Insertar si no existe
            insertStatement.setInt(1, carburante.getCarburante_Id());
            insertStatement.setString(2, carburante.getNombre());
            insertStatement.addBatch();

            // Ejecutar lote cada batchSize registros
            if (++count % batchSize == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutar el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        connection.commit();
        log.info("Datos de Carburante insertados y actualizados exitosamente.");

    } catch (SQLException e) {
        connection.rollback();
        log.error("Error al insertar o actualizar datos en la tabla Carburante", e);
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}


/*==========================================*
 *                                          *
 *          Estacion de Servicio            *
 *                                          *
 *==========================================*/

 /**
 * Convierte una cadena en un Integer. Si la cadena está vacía o es null, retorna null.
 *
 * @param value la cadena a convertir
 * @return el valor Integer o null si la cadena está vacía o es null
 */
private static Integer parseInteger(String value) {
    if (value == null || value.trim().isEmpty()) {
        return null;
    }
    try {
        return Integer.parseInt(value);
    } catch (NumberFormatException e) {
        log.error("Error al convertir a Integer: " + value, e);
        return null;
    }
}

/**
 * Convierte una cadena en un Timestamp. Si la cadena está vacía o es null, retorna null.
 *
 * @param value la cadena a convertir en formato "yyyy-MM-dd HH:mm:ss"
 * @return el valor Timestamp o null si la cadena está vacía o es null
 */
private static Timestamp parseTimestamp(String value) {
    if (value == null || value.trim().isEmpty()) {
        return null;
    }

    // Intenta dos formatos: uno con hora de dos dígitos y otro con un dígito
    DateTimeFormatter formatterWithZero = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter formatterWithoutZero = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");

    try {
        // Primero intenta con el formato HH:mm
        return Timestamp.valueOf(LocalDateTime.parse(value, formatterWithZero));
    } catch (DateTimeParseException e1) {
        try {
            // Si falla, intenta con el formato H:mm
            return Timestamp.valueOf(LocalDateTime.parse(value, formatterWithoutZero));
        } catch (DateTimeParseException e2) {
            log.error("Error al convertir a Timestamp: " + value, e2);
            return null;
        }
    }
}

/**
 * Convierte una cadena en un BigDecimal. Si la cadena está vacía o es null, retorna null.
 *
 * @param value la cadena a convertir
 * @return el valor BigDecimal o null si la cadena está vacía o es null
 */
private static BigDecimal parseBigDecimal(String value) {
    return (value == null || value.trim().isEmpty()) ? null : new BigDecimal(value.trim().replace(",", "."));
}

/**
 * Lee los datos del fichero CSV y los devuelve en una lista de Estaciones de Servicio.
 * El fichero CSV debe estar en la raíz del proyecto.
 *
 * @return - Lista de Estaciones de Servicio
 */
private static List<MySqlEstacionServicio> readEstacionServicio() {

    try (CSVReader reader = new CSVReaderBuilder(
            new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\EstacionServicio.csv"))
            .withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build())
            .build()) {

        List<MySqlEstacionServicio> estacionesServicio = new LinkedList<>();
        String[] nextLine;

        reader.skip(1); // Saltamos la primera línea del encabezado

        while ((nextLine = reader.readNext()) != null) {
            MySqlEstacionServicio estacionServicio = new MySqlEstacionServicio(
                    parseInteger(nextLine[0]),  // estacion_id
                    parseInteger(nextLine[1]),  // localidad_id
                    parseInteger(nextLine[2]),  // tipo_estacion_id
                    nextLine[3],                // codigo_postal (CP)
                    nextLine[4],                // direccion
                    parseInteger(nextLine[5]),  // margen_id
                    parseBigDecimal(nextLine[7]), // latitud
                    parseBigDecimal(nextLine[6]), // longitud
                    parseTimestamp(nextLine[8]), // toma_de_datos
                    parseInteger(nextLine[9]),  // rotulo_id
                    parseInteger(nextLine[10]), // tipo_venta_id
                    nextLine[11],               // rem
                    nextLine[12],               // horario
                    nextLine[13]                // tipo_servicio
            );
            estacionesServicio.add(estacionServicio);
        }
        return estacionesServicio;
        } catch (IOException | CsvValidationException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        }
    }
     
/* 
private static boolean doesRotuloExist(Connection connection, int rotuloId) throws SQLException {
    String sql = "SELECT 1 FROM Rotulo WHERE rotulo_id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, rotuloId);
        try (ResultSet rs = statement.executeQuery()) {
            return rs.next(); // Retorna true si se encuentra un registro
        }
    }
}
*/

/**
 * Inserta los datos en la tabla EstacionServicio.
 * Si la estación ya existe, se actualiza; si no, se inserta.
 *
 * @param connection - Conexión a la base de datos
 * @param estacionesServicio - Lista de estaciones de servicio a insertar o actualizar
 * @throws SQLException - Error al ejecutar la consulta
 */
private static void insertDataEstacionServicio(Connection connection, List<MySqlEstacionServicio> estacionesServicio) throws SQLException {
    String insertSql = "INSERT INTO EstacionServicio (estacion_id, localidad_id, tipo_estacion_id, codigo_postal, " +
            "direccion, margen_id,latitud ,longitud , toma_de_datos, rotulo_id, tipo_venta_id, rem, horario, tipo_servicio) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    int batchSize = 1500;
    int count = 0;

    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
        connection.setAutoCommit(false);
        /* 
        for (MySqlEstacionServicio estacionServicio : estacionesServicio) {
            if (doesRotuloExist(connection, estacionServicio.getRotuloId())) {
                insertStatement.setInt(1, estacionServicio.getEstacionId());
                insertStatement.setInt(2, estacionServicio.getLocalidadId());
                insertStatement.setInt(3, estacionServicio.getTipoEstacionId());
                insertStatement.setString(4, estacionServicio.getCodigoPostal());
                insertStatement.setString(5, estacionServicio.getDireccion());
                insertStatement.setObject(6, estacionServicio.getMargenId());
                insertStatement.setBigDecimal(7, estacionServicio.getLongitud());
                insertStatement.setBigDecimal(8, estacionServicio.getLatitud());
                insertStatement.setTimestamp(9, estacionServicio.getTomaDeDatos());
                insertStatement.setInt(10, estacionServicio.getRotuloId());
                insertStatement.setObject(11, estacionServicio.getTipoVentaId());
                insertStatement.setString(12, estacionServicio.getRem());
                insertStatement.setString(13, estacionServicio.getHorario());
                insertStatement.setString(14, estacionServicio.getTipoServicio());

                insertStatement.addBatch();

                //log.info("Insertando EstacionServicio " + estacionServicio.getEstacionId() + " con rotulo_id: " + estacionServicio.getRotuloId());
            } else {
                log.warn("Skipping EstacionServicio "+  estacionServicio.getEstacionId() +" with non-existing rotulo_id: " + estacionServicio.getRotuloId());
            }

            if (++count % batchSize == 0) {
                insertStatement.executeBatch();
            }
        } */
        for (MySqlEstacionServicio estacionServicio : estacionesServicio) {
                insertStatement.setInt(1, estacionServicio.getEstacionId());
                insertStatement.setInt(2, estacionServicio.getLocalidadId());
                insertStatement.setInt(3, estacionServicio.getTipoEstacionId());
                insertStatement.setString(4, estacionServicio.getCodigoPostal());
                insertStatement.setString(5, estacionServicio.getDireccion());
                insertStatement.setObject(6, estacionServicio.getMargenId());
                insertStatement.setBigDecimal(7, estacionServicio.getLongitud());
                insertStatement.setBigDecimal(8, estacionServicio.getLatitud());
                insertStatement.setTimestamp(9, estacionServicio.getTomaDeDatos());
                insertStatement.setInt(10, estacionServicio.getRotuloId());
                insertStatement.setObject(11, estacionServicio.getTipoVentaId());
                insertStatement.setString(12, estacionServicio.getRem());
                insertStatement.setString(13, estacionServicio.getHorario());
                insertStatement.setString(14, estacionServicio.getTipoServicio());

                insertStatement.addBatch();

            if (++count % batchSize == 0) {
                insertStatement.executeBatch();
            }
        }




        insertStatement.executeBatch();
        connection.commit();
        log.info("Datos de EstacionServicio insertados exitosamente.");

        
    } catch (SQLException e) {
        connection.rollback();
        log.error("Error al insertar datos en la tabla EstacionServicio", e);
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}


/*==========================================*
 *                                          *
 *           Precio Carburante              *
 *                                          *
 *==========================================*/

/**
 * Lee los datos del fichero CSV y los devuelve en una lista de Precios de Carburante.
 * El fichero CSV debe estar en la raíz del proyecto.
 *
 * @return - Lista de Precios de Carburante
 */
private static List<MySqlPrecioCarburante> readPrecioCarburante() {

    try (CSVReader reader = new CSVReaderBuilder(
            new FileReader("C:\\Users\\theha\\OneDrive\\Escritorio\\uni\\Unir\\Tercero\\1Q\\BBDDA\\Trabajos\\Primer trabajo\\PrecioCarburante.csv"))
            .withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build())
            .build()) {

        List<MySqlPrecioCarburante> preciosCarburante = new LinkedList<>();
        String[] nextLine;

        reader.skip(1); // Saltamos la primera línea del encabezado

        while ((nextLine = reader.readNext()) != null) {
            MySqlPrecioCarburante precioCarburante = new MySqlPrecioCarburante(
                    Integer.parseInt(nextLine[0]), // estacion_Id
                    Integer.parseInt(nextLine[1]), // carburante_Id
                    Double.parseDouble(nextLine[2].replace(",", "."))  // Convierte el precio a double
            );
            preciosCarburante.add(precioCarburante);
        }
        return preciosCarburante;
    } catch (IOException | CsvValidationException e) {
        log.error("Error al leer el fichero CSV", e);
        throw new RuntimeException(e);
    }
}

/**
 * Inserta los datos en la tabla PrecioCarburante.
 * Si el precio ya existe para una combinación de estación y carburante, se actualiza; si no, se inserta.
 *
 * @param connection - Conexión a la base de datos
 * @param preciosCarburante - Lista de precios de carburante a insertar o actualizar
 * @throws SQLException - Error al ejecutar la consulta
 */
private static void insertDataPrecioCarburante(Connection connection, List<MySqlPrecioCarburante> preciosCarburante) throws SQLException {
    String insertSql = "INSERT INTO PrecioCarburante (estacion_id, carburante_id, precio) VALUES (?, ?, ?)";
    String updateSql = "UPDATE PrecioCarburante SET precio = ? WHERE estacion_id = ? AND carburante_id = ?";

    // Lote para procesar las inserciones en batch
    int batchSize = 1500;
    int count = 0;

    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql);
         PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

        connection.setAutoCommit(false);

        for (MySqlPrecioCarburante precioCarburante : preciosCarburante) {
            // Para cada registro, intentamos hacer una inserción; si ya existe, se actualizará
            updateStatement.setDouble(1, precioCarburante.getPrecio());
            updateStatement.setInt(2, precioCarburante.getEstacion_Id());
            updateStatement.setInt(3, precioCarburante.getCarburante_Id());
            updateStatement.addBatch();

            insertStatement.setInt(1, precioCarburante.getEstacion_Id());
            insertStatement.setInt(2, precioCarburante.getCarburante_Id());
            insertStatement.setDouble(3, precioCarburante.getPrecio());
            insertStatement.addBatch();

            // Ejecutar lote cada batchSize registros
            if (++count % batchSize == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutar el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        connection.commit();
        log.info("Datos de PrecioCarburante insertados y actualizados exitosamente.");

    } catch (SQLException e) {
        connection.rollback();
        log.error("Error al insertar o actualizar datos en la tabla PrecioCarburante", e);
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}

}
