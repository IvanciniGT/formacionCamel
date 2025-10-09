package com.curso.camel.marshal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;

import java.util.List;

import com.curso.camel.model.PersonaOutImpl;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.springframework.boot.test.context.SpringBootTest;
import com.curso.camel.integration.AplicacionDePruebas;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;

@CamelSpringBootTest
@SpringBootTest(classes = { AplicacionDePruebas.class})// Ejecutar una aplicación Spring Boot para pruebas de integración en paralelo con las pruebas.

@ExtendWith(MockitoExtension.class)
class PersonaOutAXMLTest {

    private PersonaOutImpl personaOut;
    private DefaultExchange exchange;

    @BeforeEach
    void setUp() {
        // Antes de cada prueba, generar un nuevo Exchange y le pongo en el cuerpo del mensaje el objeto personaIn
        var camelContext = new DefaultCamelContext();
        exchange = new DefaultExchange(camelContext);
    }

    @Test
    void probarQueUnaPersonaSeConvierteAdecuadamenteAXML() throws Exception {
        configurarPersona();
        // La convierto a XML mediante el marshal
        var transformador = new JacksonXMLDataFormat();
        transformador.setUnmarshalType(PersonaOutImpl.class);
        transformador.start();
        
        // Queremos mandar ese objeto al transformador
        // El objeto nos debe devolver XML
        // El marshal necesita 3 parámetros: Exchange, objeto, y un OutputStream (null si no queremos que lo escriba en ningún sitio)
        OutputStream os = new ByteArrayOutputStream();
        transformador.marshal(exchange, personaOut, os);
        // El XML debemos comprobar que tiene los datos correctos
        String xml = os.toString();
        // Qué queremos comprobar?
        // Que el XML tiene una buena estructura
        // Donde defino la estructura que debe tener un XML?
        // En la clase? En un esquema XML : XSD
        // VErificar que mi XML cumple con el esquema XSD
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        var schema = factory.newSchema(getClass().getClassLoader().getResource("persona-out.xsd"));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new StringReader(xml)));
    }

    void configurarPersona() {
        var datosContacto = new PersonaOutImpl.DatosContactoImpl(
            List.of("123456789", "987654321"),
            List.of("pepe@example.com", "pepe@gmail.com")
        );
        var direccion = new PersonaOutImpl.DireccionImpl(
            "Calle Falsa 123",
            "Springfield",
            "12345",
            "USA"
        );
        personaOut = new PersonaOutImpl(
            "1",
            "12345678A",
            "Pepe",
            30,
            direccion,
            datosContacto
        );
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
