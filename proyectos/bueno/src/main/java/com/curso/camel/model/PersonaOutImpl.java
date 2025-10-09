package com.curso.camel.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/* Los datos que voy a despachar */
@JacksonXmlRootElement(localName = "Persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaOutImpl implements PersonaOut {
    
    @JacksonXmlProperty(localName = "Id")
    private String id;
    @JacksonXmlProperty(localName = "DNI")
    private String DNI;
    @JacksonXmlProperty(localName = "Nombre")
    private String nombre;
    @JacksonXmlProperty(localName = "Edad")
    private int edad;
    @JacksonXmlProperty(localName = "Direccion")
    private Direccion direccion;
    @JacksonXmlProperty(localName = "DatosContacto")
    private DatosContacto datosContacto;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionImpl implements Direccion {
        private String calle;
        private String ciudad;
        private String codigoPostal;
        private String pais;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatosContactoImpl implements DatosContacto {
        private List<String> telefonos;
        private List<String> emails;
    }
}