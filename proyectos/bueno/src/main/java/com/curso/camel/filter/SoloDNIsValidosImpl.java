package com.curso.camel.filter;

import org.apache.camel.Exchange;

import com.curso.camel.processor.dni.DNIProcessor;

public class SoloDNIsValidosImpl implements SoloDNIsValidos {

    @Override
    public boolean matches(Exchange exchange) {
        // Extraer la propiedad del intercambio
        Boolean esDNIValido = exchange.getProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, Boolean.class);
        return Boolean.TRUE.equals(esDNIValido);
    }

}
