package com.curso.camel.aggregator;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.curso.camel.processor.edad.EdadProcessor;

@Component
public class AgregadorDeEdadesImpl implements AgregadorDeEdades {

    public static final String SUMA_EDADES_EXCHANGE_PROPERTY_NAME = "sumaEdades";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // El old la primera vez está vacio (es null)
        // Y a partir de la segunda vez, vendrá lo que sea que hayamos devuelto en la llamada anterior
        // En el new siempre viene el nuevo mensaje que llega
        if(oldExchange == null) {
            // En newExchange tenemos el primer mensaje
            // Tiene en el body el XML de la persona
            // Y en la propiedad del exchange con la edad calculada
            // Lo que hacemos es:
            // 1. Borrar el body
            newExchange.getIn().setBody(null);
            // 1.5 Saco la edad que me ha calculado el EdadProcessor y que está guardada en una propiedad del exchange
            Integer edad = newExchange.getProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME, Integer.class);
            // 2. Crear una propiedad del exchange donde guardaremos la suma de edades
            newExchange.setProperty(SUMA_EDADES_EXCHANGE_PROPERTY_NAME, edad);
            // 3. Eliminamos la propiedad de la edad individual, que ya no nos hace falta
            newExchange.removeProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME);
            return newExchange; // Si es el primer mensaje, no hay nada que agregar, devolvemos el nuevo
        } else {
            // 1. Sacar la edad acumulada de la propiedad del oldExchange
            Integer sumaEdades = oldExchange.getProperty(SUMA_EDADES_EXCHANGE_PROPERTY_NAME, Integer.class);
            // 2. Sacar la edad del nuevo mensaje de la propiedad del newExchange
            Integer edadNueva = newExchange.getProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME, Integer.class);
            // 3. Sumar ambas edades
            sumaEdades += edadNueva;
            // 4. Guardar la nueva suma en la propiedad del oldExchange
            oldExchange.setProperty(SUMA_EDADES_EXCHANGE_PROPERTY_NAME, sumaEdades);
            return oldExchange; // Devolvemos el acumulado
        }
     
    }
    
}
