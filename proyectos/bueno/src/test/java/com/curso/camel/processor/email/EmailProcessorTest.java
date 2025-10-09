package com.curso.camel.processor.email;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;

import com.curso.camel.model.PersonaIn;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

// Con esta anotación, le decimos a JUnit que use active el motor de Mockito para gestionar los mocks
// De forma que si alguien le pone @Mock a un atributo, Mockito se encargue de crear la instancia "mockeada"
@ExtendWith(MockitoExtension.class)
class EmailProcessorTest {
    
    private EmailProcessor emailProcessor = new EmailProcessorImpl();
    private Exchange exchange;
    
    @Mock private PersonaIn personaIn;
    
    // Esta anotación, le dice a JUNIT que ejecute este método antes de cada prueba
    @BeforeEach
    void setUp() {
        // Antes de cada prueba, generar un nuevo Exchange y le pongo en el cuerpo del mensaje el objeto personaIn
        var camelContext = new DefaultCamelContext();
        exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(personaIn);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "usuario@ejemplo.com", 
        "nombre.apellido@empresa.es", 
        "test123@dominio.org",
        "contacto+tag@sitio.net",
        "admin@sub.dominio.com",
        "info_2024@ejemplo.co.uk"
    })
    void testEsEmailValido(String email) throws Exception {
        // Mockear el método getEmail() para que devuelva el email que queremos
        when(personaIn.getEmail()).thenReturn(email);
        // Cuando proceso el exchange con el procesador de Email
        emailProcessor.process(exchange);
        // Espero que en las propiedades del exchange exista la propiedad emailValido
        assertTrue(exchange.getProperties().containsKey(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME));
        // Y que esa propiedad sea true
        assertEquals(Boolean.TRUE, exchange.getProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalido", 
        "@ejemplo.com", 
        "usuario@",
        "usuario @ejemplo.com",
        "usuario@ejemplo",
        "usuario..doble@ejemplo.com",
        "usuario@.ejemplo.com",
        "usuario@ejemplo..com",
        "",
        " "
    })
    void testEsEmailNoValido(String email) throws Exception {
        // Mockear el método getEmail() para que devuelva el email que queremos
        when(personaIn.getEmail()).thenReturn(email);
        emailProcessor.process(exchange);
        assertTrue(exchange.getProperties().containsKey(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME));
        assertEquals(Boolean.FALSE, exchange.getProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME));
    }

}
