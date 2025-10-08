package com.curso.camel.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonaInImpl implements PersonaIn {
    private String id;
    private String nombre;
    private LocalDate fechaDeNacimiento;
    private String direccion;
    private String poblacion;
    private String cp;
    private String pais;
    private String telefono;
    private String email;
}
