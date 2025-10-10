package com.curso.camel.filter;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.curso.camel.processor.email.EmailProcessor;

@Component
public class SoloEmailsValidosImpl implements SoloEmailsValidos {

    @Override
    public boolean matches(Exchange exchange) {
        // Extraer la propiedad del intercambio
        Boolean esEmailValido = exchange.getProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME, Boolean.class);
        return Boolean.TRUE.equals(esEmailValido);
    }

}