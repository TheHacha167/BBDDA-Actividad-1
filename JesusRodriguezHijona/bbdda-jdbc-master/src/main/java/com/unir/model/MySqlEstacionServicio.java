package com.unir.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MySqlEstacionServicio {
    private int estacionId;
    private int localidadId;
    private int tipoEstacionId;
    private String codigoPostal;
    private String direccion;
    private Integer margenId;
    private BigDecimal longitud;
    private BigDecimal latitud;
    private Timestamp tomaDeDatos; // Para que no haya problemas con la fecha
    private int rotuloId;
    private Integer tipoVentaId;
    private String rem;
    private String horario;
    private String tipoServicio;
}
