package com.curso.camel.processor.dni;

import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;

import com.curso.camel.model.PersonaIn;

@Component
public class DNIProcessorImpl implements DNIProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        PersonaIn personaIn = exchange.getIn().getBody(PersonaIn.class);
        String dni = personaIn.getDNI();
        boolean esValido = DNIUtils.esDNIValido( dni );
        exchange.setProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, esValido);
    }
    
}
