
package com.curso.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HolaMundoCamel extends RouteBuilder {
 
    @Override
    public void configure() throws Exception { // Aquí, dentro de configure definiremos la ruta.
        // Definimos una ruta Camel
        from("timer:mi-temporizador?period=2000") // Cada 2 segundos
        
       .to("log:mensaje-recibido?showAll=true");  // Muestra to do el mensaje
    }

    // Camel tiene una integración muy desarrollada con Spring Boot.
    // Podemos definir rutas Camel como componentes de Spring.
    // Para esto solo tendremos que hacer un par de cosas:
    // 1. Este fichero, donde definimos las rutas Camel, debe extender 
    //    la clase RouteBuilder(es una clase que nos ofrece la gente de Camel).
    // 2. Que sea Spring quien genere la instancia de esta clase: @Component

    // El componente timer es un componente que emite un mensaje cada X tiempo.
    // Ese componente puede tener distintos emisores. En nuestro caso, estamos emitiendo con el buzón: mi-temporizador
    // Y cada cuanto emitimos? Cada 2000 milisegundos (2 segundos)
}