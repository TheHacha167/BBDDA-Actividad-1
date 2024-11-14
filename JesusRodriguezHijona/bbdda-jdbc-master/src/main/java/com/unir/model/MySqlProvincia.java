package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MySqlProvincia {
    private int provinciaId;
    private int comunidadId;
    private String nombre;
}
