package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MySqlMunicipio {
    private int municipioId;
    private int provinciaId;
    private String nombre;
}
