package com.curso.camel.filter;

import org.junit.jupiter.api.Test;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import com.curso.camel.model.PersonaIn;
import com.curso.camel.processor.dni.DNIProcessor;

import static org.junit.jupiter.api.Assertions.*;

class SoloDNIsValidosTest {


    private SoloDNIsValidos filtro = new SoloDNIsValidosImpl();
    private Exchange exchange;
    
    @BeforeEach
    void setUp() {
        var camelContext = new DefaultCamelContext();
        exchange = new DefaultExchange(camelContext);
    }
    
    @Test
    void testDNIValido() {
        // Preparar el intercambio con una persona con DNI válido
        exchange.setProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, true);
        // Verificar que el filtro acepta el intercambio
        assertTrue(filtro.matches(exchange));
    }

    @Test
    void testDNINoValido() {
        // Preparar el intercambio con una persona con DNI no válido
        exchange.setProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, false);
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }
    
    @Test
    void testSinValidcionDeDNI() {
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }

    @Test
    void testValidacionDNIConPropiedadNula() {
        // Preparar el intercambio sin validación de DNI
        exchange.setProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, null);
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }

    @Test
    void testValidacionDNIConPropiedadInvalida() {
        // Preparar el intercambio sin validación de DNI
        exchange.setProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, "Menchu");
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }
}
