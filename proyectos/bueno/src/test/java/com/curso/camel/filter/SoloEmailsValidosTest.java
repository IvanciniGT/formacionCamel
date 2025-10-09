package com.curso.camel.filter;

import org.junit.jupiter.api.Test;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;

import com.curso.camel.processor.email.EmailProcessor;

import static org.junit.jupiter.api.Assertions.*;

class SoloEmailsValidosTest {

    private SoloEmailsValidos filtro = new SoloEmailsValidosImpl();
    private Exchange exchange;
    
    @BeforeEach
    void setUp() {
        var camelContext = new DefaultCamelContext();
        exchange = new DefaultExchange(camelContext);
    }
    
    @Test
    void testEmailValido() {
        // Preparar el intercambio con una persona con email válido
        exchange.setProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME, true);
        // Verificar que el filtro acepta el intercambio
        assertTrue(filtro.matches(exchange));
    }

    @Test
    void testEmailNoValido() {
        // Preparar el intercambio con una persona con email no válido
        exchange.setProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME, false);
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }
    
    @Test
    void testSinValidacionDeEmail() {
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }

    @Test
    void testValidacionEmailConPropiedadNula() {
        // Preparar el intercambio sin validación de email
        exchange.setProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME, null);
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }

    @Test
    void testValidacionEmailConPropiedadInvalida() {
        // Preparar el intercambio sin validación de email
        exchange.setProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME, "Menchu");
        // Verificar que el filtro rechaza el intercambio
        assertFalse(filtro.matches(exchange));
    }
}