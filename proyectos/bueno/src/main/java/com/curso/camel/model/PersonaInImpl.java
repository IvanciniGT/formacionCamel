package com.curso.camel.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


/* Los datos que me van a llegar para su procesamiento y despacho */
@Data // Esta incluye getters setters, toString, equals, hashcode...
@NoArgsConstructor
@AllArgsConstructor
@Entity         // Esto es una entidad persistible en BBDD
@Table(name = "PERSONAS_IVAN") // Nombre de la tabla en la BBDD
public class PersonaInImpl implements PersonaIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "DNI", nullable = false, unique = true)
    private String DNI;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "FECHA_DE_NACIMIENTO", nullable = false)
    private LocalDate fechaDeNacimiento;

    @Column(name = "DIRECCION", nullable = false)
    private String direccion;

    @Column(name = "POBLACION", nullable = false)
    private String poblacion;

    @Column(name = "CP", nullable = false)
    private String cp;

    @Column(name = "PAIS", nullable = false)
    private String pais;

    @Column(name = "TELEFONO", nullable = false)
    private String telefono;

    @Column(name = "EMAIL", nullable = false)
    private String email;

}