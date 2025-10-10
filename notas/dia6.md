
ORIGEN / FROM () --> personas... de 1 en una
    |
    | n exchanges (BODY = persona)
    v
    Agregador... parte de un exchange inicial (null)... trabaja con el exchange que le llega
        La primera vez que se llama al Agregador, el exchange previo(acumulado) es null
          Y entonces el acumulador dice:
            Le establezco el cuerpo a ""
            Le genero una propiedad llamada "sumaEdades" y le pongo el valor que venga en el nuevo exchange
        La segunda vez que se llama al Agregador, el exchange previo(acumulado) ya no es null
          El previo no tendrá cuerpo (porque se lo hemos puesto a "")
          Pero tendrá la propiedad "sumaEdades" con el valor que le pusimos la vez anterior
          Entonces el acumulador dice:
            Saco el valor de la edad del nuevo exchange
            Saco el valor de la propiedad "sumaEdades" del exchange previo
            Sumo ambos valores
            Le pongo al exchange nuevo el cuerpo a """"
            Le pongo al exchange nuevo la propiedad "sumaEdades" con el valor de la suma


            NOTA... podríamos por ejemplo que el agregador nuevo, fuera acumulando los datos de las personas en el body
    |
    |   m exchanges (BODY = ""  y propiedad sumaEdades = X)
    v

Siendo n >= m
Lo normal es que n >> m
Lo que configurarmos es al agregador para que saque el mensaje acumulado cada X segundos, puedo hacerlo por número de mensajes.

    .aggregate(constant(true), new MiAgregador())
        .completionInterval(5000)  // Cada 5 segundos saca el mensaje acumulado
        .log("Mensaje acumulado: ${body} - Suma de edades: ${property.sumaEdades}")

    // Si quisieramos que en lugar de cada X segundos, fuera cada X mensajes
    .aggregate(constant(true), new MiAgregador())
        .completionSize(10)  // Cada 10 mensajes saca el mensaje acumulado
        .log("Mensaje acumulado: ${body} - Suma de edades: ${property


    .aggregate(constant(true), new MiAgregador())
        .completionInterval(5000)  // Cada 5 segundos saca el mensaje acumulado
        .completionSize(10)  // Cada 10 mensajes saca el mensaje acumulado              ESTO SI FUNCIONARIA. Lo que primero ocurra.
        .log("Mensaje acumulado: ${body} - Suma de edades: ${property.sumaEdades}")


---

# Resumiendo

.from         ----> Origen de la ruta

// Podemos ir aplicando Procesadores, filtros...
    .processor( new MiProcessor() )
    .bean( new MiBean() )
    .filter().method( new MiFiltro(), "matches" )
    .to("uri:destino")

// Muchas veces necesitamos control de flujo
    .choice()
        .when( simple("${body} contains 'XXX'") )
            .log("El body contiene XXX")
        .when( simple("${body} contains 'YYY'") )
            .log("El body contiene YYY")
        .otherwise()
            .log("El body no contiene ni XXX ni YYY")
    .end()

// Otras veces necesitamos agrupar mensajes
    .aggregate( constant(true), new MiAgregador() )
        .completionInterval(5000)  // Cada 5 segundos saca el mensaje acumulado
        .completionSize(10)  // Cada 10 mensajes saca el mensaje acumul


// Duplicar el mensaje para tratamientos independientes
    .multicast()
        .to("uri:destino1", "uri:destino2", "uri:destino3")
    .end()   // Los to() se lanzan secuencialmente, y espera a que los 3 destinos hayan terminado para continuar
    .log("Los 3 destinos han terminado")

    .wireTap("uri:destino") // No espera a que el destino termine. Continúa inmediatamente
    .log("El mensaje ha sido enviado al destino")

// Trabajo en paralelo dentro de una ruta
    multicast().parallelProcessing()
        .to("uri:destino1", "uri:destino2", "uri:destino3")
    .end()   // Los to() se lanzan en paralelo, pero espera a que los 3 destinos hayan terminado para continuar
    .log("Los 3 destinos han terminado")

// En caso de los processors, podemos capturar sus Excepciones con un 
    .transacted()
    .onException(Exception.class)
        .log("Se ha producido una excepción: ${exception.message}")
        .handled(true) // Con esto le digo que la excepción ha sido resuelta
        .handled(false) // Con esto le digo que la excepción aunque le he dado un tratamiento, no está resuelta
        .markRollbackOnly() // Si estamos en una transacción, marca la transacción para que se haga rollback
    .end()

// Al enviar a destinos, si hay una excepción, la exchange se para o no , dependiendo del handled
    .doTry()
        .to("uri:destino1")
    .doCatch( Exception.class )
        .log("Se ha producido una excepción: ${exception.message}")
        .handled(true) // Con esto le digo que la excepción ha sido resuelta
        .markRollbackOnly() // Si estamos en una transacción, marca la transacción para que se haga rollback
    .end()

// SEDA. Seda es una cola de almacenamiento de datos en memoria
// Nos sirve para desacoplar el procesamiento de mensajes... y partir rutas complejas en rutas más sencillas
// O para hacer procesamiento en paralelo
    //RUTA 1:
    .to("seda:cola1")
    .log("Mensaje enviado a la cola seda: ${body}")

    ...

    Y en otra ruta:
    // Ruta 2
    from("seda:cola1")
        .tramite de los mensajes

Podemos tener varias rutas que estén conectadas a la misma 
    .to("seda:cola1")


/// Bucles
    .loop(5)  // Repite 5 veces el bloque
        .log("Dentro del bucle")
    .end()

    .loopDoWhile( simple("${body} contains 'XXX'") ) // Mientras el body contenga XXX, repite el bloque
        .log("Dentro del bucle")
    .end()

    .loopWhile( simple("${body} contains 'XXX'") ) // Mientras el body contenga XXX, repite el bloque
        .log("Dentro del bucle")
    .end()


    A veces me interesan para si me falla una petición HTTP, reintentarla X veces


    .stop()  // Detiene el procesamiento del exchange en este punto
    // Ese lo podemos meter en choice, para según la condición, continuar o no