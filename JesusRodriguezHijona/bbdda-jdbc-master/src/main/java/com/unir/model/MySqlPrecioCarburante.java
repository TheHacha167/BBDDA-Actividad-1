package com.unir.model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MySqlPrecioCarburante {
    private int estacion_Id;;
    private int carburante_Id;
    private double  precio;
}
