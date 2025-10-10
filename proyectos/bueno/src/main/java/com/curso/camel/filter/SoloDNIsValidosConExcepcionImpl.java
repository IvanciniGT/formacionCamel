package com.curso.camel.filter;

import org.apache.camel.Exchange;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.curso.camel.processor.dni.DNIProcessor;

@Component
// Esto no lo haríamos en un proyecto real..
// Si quiero que éste ahora sustituya al otro... El otro lo borro.
// Y si el día de mañana me arrepiento? Para eso tengo git!
// Con esto no funcionarían las pruebas como las tenemos definidas...
// Pero para el ejemplo de la ruta mas compleja, nos ayuda a generar excepciones... que poder tramitar
public class SoloDNIsValidosConExcepcionImpl implements SoloDNIsValidos {

    @Override
    public boolean matches(Exchange exchange) {
        // Extraer la propiedad del intercambio
        Boolean esDNIValido = exchange.getProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME, Boolean.class);
        if (esDNIValido == null || !esDNIValido) {
            throw new IllegalArgumentException("DNI no válido o no procesado");
        }
        return Boolean.TRUE.equals(esDNIValido);
    }

}
