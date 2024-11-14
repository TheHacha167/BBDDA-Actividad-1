package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MySqlLocalidad {
    private int localidadId;
    private int municipioId;
    private String nombre;
}
