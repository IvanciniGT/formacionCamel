package com.curso.camel.marshal;

import org.apache.camel.test.junit5.params.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;

import java.util.List;

import com.curso.camel.model.PersonaOut;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


@ExtendWith(MockitoExtension.class)
class PersonaOutAXMLTest {

    @Mock private PersonaOut personaOut;

    @Test
    void probarQueUnaPersonaSeConvierteAdecuadamenteAXML() throws Exception {
        configurarPersona();
        // La convierto a XML mediante el marshal

        var transformador = new JacksonXMLDataFormat();
        transformador.setUnmarshalType(PersonaOut.class);
        
        // Queremos mandar ese objeto al transformador
        // El objeto nos debe devolver XML

        transformador.start();

        // El marshal necesita 3 parámetros: Exchange, objeto, y un OutputStream (null si no queremos que lo escriba en ningún sitio)
        OutputStream os = new ByteArrayOutputStream();

        transformador.marshal(null,  personaOut, os);

        // El XML debemos comprobar que tiene los datos correctos
        String xml = os.toString();
        System.out.println(xml);
    }

    void configurarPersona() {
        when(personaOut.getId()).thenReturn("1");
        when(personaOut.getDNI()).thenReturn("12345678Z");
        when(personaOut.getNombre()).thenReturn("Pepe");
        when(personaOut.getEdad()).thenReturn(30);
        when(personaOut.getDireccion()).thenReturn(new PersonaOut.Direccion() {
            public String getCalle() { return "Calle Falsa 123"; }
            public void setCalle(String calle) {}
            public String getCiudad() { return "Springfield"; }
            public void setCiudad(String ciudad) {}
            public String getCodigoPostal() { return "28080"; }
            public void setCodigoPostal(String codigoPostal) {}
            public String getPais() { return "USA"; }
            public void setPais(String pais) {}
        });
        when(personaOut.getDatosContacto()).thenReturn(new PersonaOut.DatosContacto() {
            public List<String> getTelefonos() { return List.of("123456789", "987654321"); }
            public void setTelefonos(java.util.List<String> telefonos) {}
            public List<String> getEmails() { return List.of("pepe@example.com", "pepe@gmail.com"); }
            public void setEmails(List<String> emails) {}   
        });
    }
    
}
/*
public interface PersonaOut {
    
    String getId();
    void setId(String id);
    
    String getDNI();
    void setDNI(String dni);
    
    String getNombre();
    void setNombre(String nombre);
    
    int getEdad();
    void setEdad(int edad);

    Direccion getDireccion();
    void setDireccion(Direccion direccion);

    DatosContacto getDatosContacto();
    void setDatosContacto(DatosContacto datosContacto);

    interface Direccion {
        String getCalle();
        void setCalle(String calle);
        
        String getCiudad();
        void setCiudad(String ciudad);
        
        String getCodigoPostal();
        void setCodigoPostal(String codigoPostal);
        
        String getPais();
        void setPais(String pais);
    }

    interface DatosContacto {
        List<String> getTelefonos();
        void setTelefonos(List<String> telefonos);
        
        List<String> getEmails();
        void setEmails(List<String> emails);
    }
}
 */
