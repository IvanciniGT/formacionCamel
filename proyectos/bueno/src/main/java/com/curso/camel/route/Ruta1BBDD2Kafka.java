package com.curso.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import com.curso.camel.processor.edad.EdadProcessor;
import com.curso.camel.processor.dni.DNIProcessor;
import com.curso.camel.processor.email.EmailProcessor;
import com.curso.camel.filter.SoloDNIsValidos;
import com.curso.camel.filter.SoloEmailsValidos;
import com.curso.camel.mapper.PersonaIn2PersonaOutMapper;


@Component
public class Ruta1BBDD2Kafka extends RouteBuilder {

    private final String origen;
    private final String destino;

    public Ruta1BBDD2Kafka( 
        @Value("${ruta1.origen}") String origen, 
        @Value("${ruta1.destino}") String destino
    ) {
        this.origen = origen;
        this.destino = destino;
    }

    @Override
    public void configure() throws Exception {
        from(origen)                                                // Lee los registros de la base de datos
            .log("Registro leído de la base de datos: ${body}")
             // Qué tipo de dato tendremos en el Exchange en este punto: PersonaIn
            .bean(   EdadProcessor.class                  )         // Calcula la edad y la añade una propiedad del exchange
            .bean(   DNIProcessor.class                   )         // Validar el DNI y guarda el dato en una propiedad del exchange
            //.filter( SoloDNIsValidos.class                )         // Filtra los registros con DNI inválido
            .bean(   EmailProcessor.class                 )         // Valida el email e introduce una propiedad en el exchange
            //.filter( SoloEmailsValidos.class              )         // Filtra los registros con email inválido
            .bean(   PersonaIn2PersonaOutMapper.class     )         // Convierte PersonaIn a PersonaOut // MAPEADOR
            //.marshal( new JacksonXMLDataFormat() )                  // Convierte el body a JSON
            .log("Mensaje JSON a enviar a Kafka: ${body}")
            .to(      destino                        );             // Envía el mensaje a Kafka
    }
}
