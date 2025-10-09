package com.curso.camel.processor.edad;

import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;

import com.curso.camel.model.PersonaIn;

@Component
public class EdadProcessorImpl implements EdadProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        PersonaIn personaIn = exchange.getIn().getBody(PersonaIn.class);
        var fechaDeNacimiento = personaIn.getFechaDeNacimiento();
        int edad = EdadUtils.calcularEdad(fechaDeNacimiento);
        exchange.setProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME, edad);
    }
    
}