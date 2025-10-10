package com.curso.camel.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/* Los datos que voy a despachar */
@JacksonXmlRootElement(localName = "Persona")
@Getter
@Setter
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@AllArgsConstructor
public class PersonaOutImpl implements PersonaOut {
    
    @JacksonXmlProperty(localName = "id", isAttribute = true)
    private Long id;
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
        @JacksonXmlProperty(localName = "Calle")
        private String calle;
        @JacksonXmlProperty(localName = "Ciudad")
        private String ciudad;
        @JacksonXmlProperty(localName = "CodigoPostal")
        private String codigoPostal;
        @JacksonXmlProperty(localName = "Pais")
        private String pais;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatosContactoImpl implements DatosContacto {
        @JacksonXmlElementWrapper(localName = "Telefonos")
        @JacksonXmlProperty(localName = "Telefono")
        private List<String> telefonos;
        @JacksonXmlElementWrapper(localName = "Emails")
        @JacksonXmlProperty(localName = "Email")
        private List<String> emails;
    }
}