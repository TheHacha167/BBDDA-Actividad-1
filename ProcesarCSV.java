import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProcesarCSV {
    public static void main(String[] args) {
        String nombreArchivoEntrada = "input.csv"; // Reemplaza con la ruta de tu archivo CSV de entrada
        String nombreArchivoSalida = "output.csv"; // Archivo donde se guardará el resultado

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivoSalida))) {

            // Leer la primera línea (cabecera)
            String lineaCabecera = br.readLine();
            if (lineaCabecera == null) {
                System.err.println("El archivo CSV está vacío.");
                return;
            }
            String[] cabeceras = lineaCabecera.split(";");

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 0) {
                    continue; // Saltar líneas vacías
                }
                String idGasolinera = datos[0];
                for (int i = 1; i < datos.length; i++) {
                    String precio = datos[i].trim();
                    if (!precio.isEmpty()) {
                        String tipoCombustible = cabeceras[i];
                        String resultado = idGasolinera + ";" + tipoCombustible + ";" + precio;
                        bw.write(resultado);
                        bw.newLine();
                    }
                }
            }
            System.out.println("El archivo ha sido procesado y guardado en " + nombreArchivoSalida);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
