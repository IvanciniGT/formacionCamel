package com.curso.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import com.curso.camel.processor.edad.EdadProcessor;
import com.curso.camel.processor.dni.DNIProcessor;
import com.curso.camel.processor.email.EmailProcessor;
import com.curso.camel.aggregator.AgregadorDeEdades;
import com.curso.camel.filter.SoloDNIsValidos;
import com.curso.camel.filter.SoloEmailsValidos;
import com.curso.camel.mapper.PersonaIn2PersonaOutMapper;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;

import com.curso.camel.aggregator.AgregadorDeEdades;


@Component
public class Ruta1BBDD2Kafka extends RouteBuilder {

    public static final String RUTA_ID = "ruta1-bbdd2kafka";

    private final String origen;
    private final String destino;
    private final String destino2;
    private final AgregadorDeEdades agregadorDeEdades;

    public Ruta1BBDD2Kafka( 
        @Value("${ruta1.origen}") String origen, 
        @Value("${ruta1.destino}") String destino,
        @Value("${ruta1.destino2}") String destino2,
        AgregadorDeEdades agregadorDeEdades
    ) {
        this.origen = origen;
        this.destino = destino;
        this.destino2 = destino2;
        this.agregadorDeEdades = agregadorDeEdades;
    }

    @Override
    public void configure() throws Exception {
        from(origen)                                                // Lee los registros de la base de datos
            .routeId( RUTA_ID ) // Esto me permitirá referirme a la ruta por su id desde otros sitios.
            // Esto es util para arrancar, parar, ver estadísticas, O EJECUTAR PRUEBAS de una ruta.
            .log("Registro leído de la base de datos: ${body}")
             // Qué tipo de dato tendremos en el Exchange en este punto: PersonaIn
            .bean( EdadProcessor.class          )                   // Calcula la edad y la añade una propiedad del exchange
            .bean( DNIProcessor.class           )                   // Validar el DNI y guarda el dato en una propiedad del exchange
            .filter().method( SoloDNIsValidos.class, "matches" )    // Filtra los registros con DNI inválido
            .bean( EmailProcessor.class         )                   // Valida el email e introduce una propiedad en el exchange
            .filter().method( SoloEmailsValidos.class, "matches" )  // Filtra los registros con email inválido
            .bean( PersonaIn2PersonaOutMapper.class )               // Convierte PersonaIn a PersonaOut // MAPEADOR
            .marshal( new JacksonXMLDataFormat() )                  // Convierte el body a XML
            .log("Mensaje XML a enviar a Kafka: ${body}")
            // Este trabajo se haría en forma secuencial
            //.to(      destino                        )             // Envía el mensaje a Kafka
            //.to(      destino2                        );             // Envía el mensaje a Kafka
            // Pero para aprovechar mejor los recursos, lo haremos en paralelo
            .multicast().parallelProcessing()
                .to( destino  )                                     // Envía el mensaje a Kafka
                .to( destino2 )                                     // Envía el mensaje a otro topic de Kafka
                // Podríamos usar esto para procesar por un lado los DNIs y por otro los emails.
                // POCO GANARIA... El reparto de trabajo al final se hace entre mis cores...
                // O dedico los cores a una cosa o los dedico a otra.
                // Si proceso 2 mensajes en paralelo.
                // Puedo tener 2 hilos, uno ejecutando el procesamiento de DNIs y otro el de emails.
                // O puedo tener 2 hilos, ejecutando cada uno el procesamiento de DNIs y emails de cada dato.
                //  Al final el tiempo de computación TOTAL es el mismo.
                // Esto tiene gracia cuando los procesos que se están lanzando involucran WAITS.
                // Mando los datos a la BBDD y a Kafka.
                // Y Cada uno me tiene que dar confirmación... y entre tanto yo esperando como un tonto.
            .end() // Este end es un JOIN de los dos hilos de ejecución
            .log("Mensaje XML enviado a Todos los destinos"); // Este no se ejecuta hasta que ambos envíos han terminado
            /*        
        from(origen)
            .routeId( RUTA_ID ) 
            .transacted()
            .log("Registro leído de la base de datos: ${body}")
            // Eso lo haríamos sobre un procesador que pudiera lanzar excepciones
            /*
            .onException(IllegalArgumentException.class) 
             // Aqui es donde puedo darle un tratamiento especial a la excepción
                    .log("Ha habido un error en el procesamiento del DNI: ${exception.message}")
                    // Podríamos llevarlo a otro sistema (cola seda, kafka, BBDD, fichero...)
                    .markRollbackOnly() // Con esto le digo que la transacción tiene que hacer rollback
                    .handled(true) // Con esto le digo que la excepción ya está tratada
                    // handled(false) // Con esto le digo que la excepción aunque le he dado un tratamiento, no está resuelta
                    // POdría pasarnos, como es nuestro caso, en un filtro o en un to
            .bean( DNIProcessor.class           )
            // Manejamos la excepción solo para el filtro con doTry/doCatch
            .doTry()
                .filter().method( SoloDNIsValidos.class, "matches" )
                .to("seda:procesamientoPosterior") // Desacoplo la lectura de la BBDD del resto de procesamiento
                                                    // Esto me permite que la lectura de la BBDD no se vea afectada por el tiempo que tarden
                                                    // Ahi se genera una cola interna en memoria (seda)
                                                    // Si después del seda pusieramos algo, 
                                                    // ese algo se ejecutaría una vez que el seda haya terminado de encolar el mensaje
                                                    // Seda nos sirve para dejar algo en una cola... y que de ahí se consuma cuando se pueda
                                                    // De forma ya independiente del productor
            .endDoTry()
            .doCatch(IllegalArgumentException.class)
                .log("ERROR: DNI no válido - ${exception.message}")
                // Podríamos enviar a una cola de errores: .to("seda:errores")
                .markRollbackOnly() // Marcamos la transacción para rollback
            .end() // Cierra el doTry
            .log("Mensaje encolado en seda: ${body}"); // Este log se ejecutaría inmediatamente después del encolado

        from("seda:procesamientoPosterior")
            .routeId( RUTA_ID + "-proc" )
            .bean( EdadProcessor.class          )
            .bean( EmailProcessor.class         )
            .filter().method( SoloEmailsValidos.class, "matches" ) 
            .bean( PersonaIn2PersonaOutMapper.class )     
            //.to("seda:acumularEdades") // Desacoplo el envío a Kafka del resto de procesamiento
                                           // Hasta que no encole el mensaje, no sigo con el procesamiento
                                           // Tendríamos una opción mejor:
            .wireTap("seda:acumularEdades") // Esto es como un tee de fontanería
                                           // Coge el mensaje que llega y hace una copia
                                           // Esa copia la envía a la ruta del wireTap... pero SIN ESPERAR
                                           // En este caso simplemente me evito esperar a que se encole el mensaje... qué es mínimo...
                                           // En otros casos...(escribir a una cola Kafka, escribir a una BBDD...) puede ser mucho tiempo
            .marshal( new JacksonXMLDataFormat() )                 
            .log("Mensaje XML a enviar a Kafka: ${body}")
            .multicast()                  // Lo que hace es generar 2 copias del mensaje
                .parallelProcessing()     // Esas copias se procesan en paralelo
                .to( destino  )                                    
                .to( destino2 )                                    
            .end()
            .log("Mensaje XML enviado a Todos los destinos");

        from("seda:acumularEdades")
            .routeId( RUTA_ID + "-acum" )
            .log("Registro leído de la base de datos para acumulación de edades: ${body}")
             // Cada mensaje que llega tiene en una propiedad del exchange la edad de la persona
            .aggregate(
                constant(true), // Siempre el mismo id de agregación, por lo que todos los mensajes van al mismo grupo
                agregadorDeEdades 
            )
            // Y ahora configuro cada cuanto se saca el mensaje acumulado (en tiempo o nº mensajes)
            .completionSize(10) // Cada 10 mensajes que lleguen, saco el acumulado
            .completionTimeout(5000) // O si pasan 5 segundos desde que se inició la agregación, saco el acumulado
            .log("Acumulado de edades: ${exchangeProperty.sumaEdades}");
*/

        // Lo que vamos a hacer ahora es ir sumando las edades de la gente que vamos leyendo
        // Como ejemplo es un sinsentido, para que quiero sumar edades de gente .
        // Podrían ser pedidos, facturas, importes...
        // Para esto vamos a usar un agregador de mensajes
        // El agregador lo que hace es ir acumulando mensajes en un solo mensaje
        // Recibe un exchange y lo combina con el exchange que el va generando acumulado
        // Y cada X tiempo o cuando se le dice, saca el mensaje acumulado
        // Y empieza a acumular de nuevo
    }

    // Pruebas de integración necesarias:
    // - ProcesadorDNI -> FiltroDNI.      OK!
    // - ProcesadorEmail -> FiltroEmail   TO DO
    // - ProcesadorEdad -> Mapeador.      TO DO
}
