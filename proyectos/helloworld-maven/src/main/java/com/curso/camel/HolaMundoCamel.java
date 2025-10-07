
package com.curso.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

    // Camel tiene una integración muy desarrollada con Spring Boot.
    // Podemos definir rutas Camel como componentes de Spring.
    // Para esto solo tendremos que hacer un par de cosas:
    // 1. Este fichero, donde definimos las rutas Camel, debe extender 
    //    la clase RouteBuilder(es una clase que nos ofrece la gente de Camel).
    // 2. Que sea Spring quien genere la instancia de esta clase: @Component

@Component
public class HolaMundoCamel extends RouteBuilder {
 
    private int contador = 0;
    private final Processor procesadorBaconIpsum;
    
    // Constructor para inyección de dependencias
    public HolaMundoCamel(@Qualifier("ProcesadorRuta2") Processor procesadorBaconIpsum) {
        this.procesadorBaconIpsum = procesadorBaconIpsum;
        System.out.println("HolaMundoCamel creado con ProcesadorBaconIpsum inyectado");
    }

    @Override
    public void configure() throws Exception { // Aquí, dentro de configure definiremos la ruta.

        //////////////////////////////////////////////////////////////////////////////////////////////////
        // RUTA 1
        //////////////////////////////////////////////////////////////////////////////////////////////////
        // Definimos una ruta Camel
        // From(): Método para indicar el origen del mensaje
        // timer:mi-temporizador?period=2000: Diciendo que nuestros mensajes provienen de un temporizador, que vamos a configurar con un 
        // nombre (mi-temporizador) y que va a emitir un mensaje cada 2000 milisegundos (2 segundos)
        // El nombre del temporizador me servirá para determinar el origen del mensaje.
        from("timer:mi-temporizador?period=2000") // Cada 2 segundos
        
        // to() indica el destino del mensaje... una vez llegado a este destino hemos terminado el procesamiento de la ruta, para 
        // ese dato mensaje.
        // log:mensaje-recibido: Indica que el destino del mensaje es un log
        // Ese log en nuestro caso se llamará mensaje-recibido
        // ?showAll=true: Indica que queremos que se muestre tod o el mensaje (headers, body, etc)
        // 
       .to("log:mensaje-recibido?showAll=true");  // Muestra to do el mensaje


        //////////////////////////////////////////////////////////////////////////////////////////////////
        // RUTA 2
        //////////////////////////////////////////////////////////////////////////////////////////////////
        from("timer:timer-ruta-2?period=5000")
            // En esta linea digo que QUIERO (lenguaje Declarativo) tener un temporalizador que emita un mensaje cada 5 segundos
            // El resultado.. lo que ocurrirá, es que cuando arranque mi programa se generá un contexto de intercambio (un exchange)
            // Cada 5 segundos que contendrá información de un mensaje (un mensaje vacío, con body null y unos headers igual de vacios)
       // Vamos a añadir un header con el contador incrementado
         //.setHeader("Número de Mensaje", () -> ++contador) // Cada vez que pase por aquí se incrementa el contador y se añade al header
                 // variable++ -> devuelve el valor de variable y luego lo incrementa para la siguiente.. lo deja incrementado
                 // ++variable -> incrementa la variable y luego devuelve el valor incrementado
         // Esa sintaxis ha sido una sintaxis ultrareducida ... gracias a que tenemos una función que ya devuelve el valor que queremos
         // Eso mismo lo podríamos hacer con más código... creando un procesador customizado
         //.process(new Processor() {
         //    @Override
         //    public void process(Exchange exchange) throws Exception {
         //       Message mensajeEntrante = exchange.getIn();
         //       mensajeEntrante.setHeader("Número de Mensaje", ++contador);
         //    }
         //})
         // Podríamos montar lo mismo con una expresión lambda
         //   .process(exchange -> exchange.getIn().setHeader("Número de Mensaje", ++contador) )

        // Si hay una función específica (atajo) mejor que mejor... Menos código, menos probabilidades de error
        // Si no la hay:
        //     Para cosas extraordinariamente simples: Usar una expresión lambda
        //     Para cosas más complejas: Crear una clase que implemente Processor que configuraremos como un bean de Spring y la inyectaremos en el constructor de esta clase

        // Vamos a hacer un primer ejemplo. BaconIpsum
        // Queremos un programa que asigne mensajes LorenIpsum(BaconIpsum) al body del mensaje ... mensajes que sacará de una petición HTTP
        // Ese programa estará definido en una clase, que daremos de alta como componente de Springboot
        // Esa clase tendrá un método que hará la petición HTTP y devolverá el exchange con el body modificado
        // La inyectaremos aquí... con un qualificador para que sepa qué clase inyectar... porque es más que probable que tengamos
        // muchos componentes de tipo Processor (Componente procesor admisibles por esta ruta: @ProcesadorRuta2)

       .process(procesadorBaconIpsum)  // Usar el procesador inyectado que obtiene contenido de BaconIpsum
       .to("log:RUTA-2?showAll=true");  // Muestra to do el mensaje




    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // RUTA 3
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Queremos una ruta que genere mensajes cada 2 segundos 
    // A cada mensaje le ponemos un header... TIPO 1 o TIPO 2 (alternando)
    // Y escribimos a log
    from("timer:timer-ruta-3?period=2000")
        .setHeader("Tipo-Mensaje", () ->  "TIPO " + (contador++ % 3))
        // Con lenguaje simple
        //.setHeader("Tipo-Mensaje", simple("${exchangeId} % 2 == 0 ? 'TIPO 1' : 'TIPO 2'"))

        // FILTRADO... me quiero quedar solo con mensajes de TIPO 1
        // Si añado varios filtros, se deben cumplir todos y cada uno de ellos.
        // SON ANDs
        // Definimos en el filtro lo que quiero mantener
        //.filter(header("Tipo-Mensaje").isEqualTo("TIPO 1")) // De nuevo aquí tenemos sintaxis de esa edulcorada.. muy específica de Camel
        // Si no existiera esa sintaxis... podríamos usar una expresión lambda
        //.filter(exchange -> "TIPO 1".equals(exchange.getIn().getHeader("Tipo-Mensaje")))
        //.to("log:RUTA-3?showAll=true");

        // Algo diferente a los filtros, sería el enrutado.
        // Los mensajes de tipo 1, al log de tipo 1
        // Los mensajes de tipo 2, al log de tipo 2
        // El resto a un log de tipo OTRO
        .choice() // Inicio del enrutado condicional. Nos permite establecer distintos flujos de procesamiento de datos.
        // No solo distintos TO, sino también distintos procesamientos en base a algo
            .when(header("Tipo-Mensaje").isEqualTo("TIPO 1"))
                .to("log:RUTA-3-TIPO-1?showAll=true")
            //.when(header("Tipo-Mensaje").isEqualTo("TIPO 2")) // En lugar de con sintaxis DSL, con una lambda
            .when(exchange -> "TIPO 2".equals(exchange.getIn().getHeader("Tipo-Mensaje")))
                .to("log:RUTA-3-TIPO-2?showAll=true")
            .otherwise()
                .setHeader("Motivo", constant("No es ni TIPO 1 ni TIPO 2")) // procesamiento adicional
                .to("log:RUTA-3-OTRO?showAll=true")
        .end(); // Fin del enrutado condicional


    }
    // PREGUNTA!
    // Le veis alguna gracia a esto de los BEANS en CAMEL? PRUEBAS !
}

// RECETAS PARA USAR UNA IA para escribir código:
// 1. No dejar a la IA que escriba ni una triste linea de código que no hayáis revisado previamente
// 2. No dejar a la IA que escriba ni una triste linea de código sin que tenga un buen CONTEXTO
//.    No me importa estar 10 minutos hablando con la IA... sin que haga nada... Solo que me entienda lo que quiero.
// 3. Quitarle importancia al prompt... NO ES LO IMPORTANTE !
//    En el prompt le pido lo que quiero... le doy la orden.


// UNA IA se alimenta de un prompt y de un CONTEXTO
// El CONTEXTO es lo importante... es lo que va a permitir que la IA genere
// EL CONTEXTO ES:
// - LO QUE TIENE LA IA DE ACCESO A MI CÓDIGO: Mi proyecto, mis clases, mis ficheros de configuración, mis dependencias, etc
// - El historial de mi conversación con ella