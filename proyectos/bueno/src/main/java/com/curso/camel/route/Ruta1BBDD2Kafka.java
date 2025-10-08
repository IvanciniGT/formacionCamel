package com.curso.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Ruta1BBDD2Kafka extends RouteBuilder {

    private final String origen;
    private final String destino;
    private final EdadProcessor edadProcessor;
    private final DNIProcessor dniProcessor;
    private final EmailProcessor emailProcessor;
    private final PersonaIn2PersonaOutMapper personaInToPersonaOutProcessor;
    private final SoloDNIsValidos soloDNIsValidos;
    private final SoloEmailsValidos soloEmailsValidos;

    public Ruta1BBDD2Kafka( 
        @Value("${ruta1.origen}") String origen, 
        @Value("${ruta1.destino}") String destino ,
        EdadProcessor edadProcessor,
        DNIProcessor dniProcessor,
        EmailProcessor emailProcessor,
        PersonaIn2PersonaOutMapper personaInToPersonaOutProcessor,
        SoloDNIsValidos soloDNIsValidos,
        SoloEmailsValidos soloEmailsValidos
        ) {
        this.origen = origen;
        this.destino = destino;
        this.edadProcessor = edadProcessor;
        this.dniProcessor = dniProcessor;
        this.emailProcessor = emailProcessor;
        this.personaInToPersonaOutProcessor = personaInToPersonaOutProcessor;
        this.soloDNIsValidos = soloDNIsValidos;
        this.soloEmailsValidos = soloEmailsValidos;
    }

    @Override
    public void configure() throws Exception {
        from(origen)                                                // Lee los registros de la base de datos
            .log("Registro leído de la base de datos: ${body}")
             // Qué tipo de dato tendremos en el Exchange en este punto: PersonaIn
            .process( edadProcessor                  )              // Calcula la edad y la añade una propiedad del exchange
            .process( dniProcessor                   )              // Validar el DNI y guarda el dato en una propiedad del exchange
            .filter(  soloDNIsValidos                )              // Filtra los registros con DNI inválido
            .process( emailProcessor                 )              // Valida el email e introduce una propiedad en el exchange
            .filter(  soloEmailsValidos              )              // Filtra los registros con email inválido
            .process( personaInToPersonaOutProcessor )              // Convierte PersonaIn a PersonaOut // MAPEADOR
            .marshal().xml()                                        // Convierte el body a XML
            .log("Mensaje JSON a enviar a Kafka: ${body}")
            .to(      destino                        );             // Envía el mensaje a Kafka
    }
}
