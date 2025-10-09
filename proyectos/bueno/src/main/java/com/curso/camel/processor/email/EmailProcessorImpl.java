package com.curso.camel.processor.email;

import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;

import com.curso.camel.model.PersonaIn;

@Component
public class EmailProcessorImpl implements EmailProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        PersonaIn personaIn = exchange.getIn().getBody(PersonaIn.class);
        String email = personaIn.getEmail();
        boolean esValido = EmailUtils.esEmailValido( email );
        exchange.setProperty(EmailProcessor.EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME, esValido);
    }
    
}
