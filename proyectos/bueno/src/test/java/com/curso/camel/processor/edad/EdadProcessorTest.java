package com.curso.camel.processor.edad;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
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
import java.time.LocalDate;

// Con esta anotación, le decimos a JUnit que use active el motor de Mockito para gestionar los mocks
// De forma que si alguien le pone @Mock a un atributo, Mockito se encargue de crear la instancia "mockeada"
@ExtendWith(MockitoExtension.class)
class EdadProcessorTest {
    
    private EdadProcessor edadProcessor = new EdadProcessorImpl();
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
    @ValueSource(ints = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50})
    void testCalcularEdadConFechasDinamicas(int anosAtras) throws Exception {
        int anoActual = LocalDate.now().getYear();
        LocalDate fechaNacimiento = LocalDate.of(anoActual - anosAtras, 6, 15); // Mes 6, día 15 para evitar edge cases
        
        // Mockear el método getFechaDeNacimiento() para que devuelva la fecha que queremos
        when(personaIn.getFechaDeNacimiento()).thenReturn(fechaNacimiento);
        // Cuando proceso el exchange con el procesador de Edad
        edadProcessor.process(exchange);
        // Espero que en las propiedades del exchange exista la propiedad edad
        assertTrue(exchange.getProperties().containsKey(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME));
        // Y que esa propiedad sea un entero con la edad calculada
        Integer edadCalculada = (Integer) exchange.getProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME);
        assertNotNull(edadCalculada);
        assertTrue(edadCalculada >= anosAtras - 1 && edadCalculada <= anosAtras,
                  "Para fecha " + fechaNacimiento + " (hace " + anosAtras + " años), edad calculada: " + edadCalculada);
    }

    @Test
    void testCalcularEdadConFechaDeHoy() throws Exception {
        LocalDate hoy = LocalDate.now();
        // Mockear el método getFechaDeNacimiento() para que devuelva la fecha de hoy
        when(personaIn.getFechaDeNacimiento()).thenReturn(hoy);
        edadProcessor.process(exchange);
        assertTrue(exchange.getProperties().containsKey(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME));
        assertEquals(0, exchange.getProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME));
    }

    @Test
    void testCalcularEdadConFechaNula() throws Exception {
        // Mockear el método getFechaDeNacimiento() para que devuelva null
        when(personaIn.getFechaDeNacimiento()).thenReturn(null);
        edadProcessor.process(exchange);
        assertTrue(exchange.getProperties().containsKey(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME));
        assertEquals(0, exchange.getProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME));
    }

}