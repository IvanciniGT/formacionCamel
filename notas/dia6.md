
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

> Podemos ir aplicando Procesadores, filtros...
    .processor( new MiProcessor() )
    .bean( new MiBean() )
    .filter().method( new MiFiltro(), "matches" )
    .to("uri:destino")

> Muchas veces necesitamos control de flujo
    .choice()
        .when( simple("${body} contains 'XXX'") )
            .log("El body contiene XXX")
        .when( simple("${body} contains 'YYY'") )
            .log("El body contiene YYY")
        .otherwise()
            .log("El body no contiene ni XXX ni YYY")
    .end()

> Otras veces necesitamos agrupar mensajes
    .aggregate( constant(true), new MiAgregador() )
        .completionInterval(5000)  // Cada 5 segundos saca el mensaje acumulado
        .completionSize(10)  // Cada 10 mensajes saca el mensaje acumul


> Duplicar el mensaje para tratamientos independientes
    .multicast()
        .to("uri:destino1", "uri:destino2", "uri:destino3")
    .end()   // Los to() se lanzan secuencialmente, y espera a que los 3 destinos hayan terminado para continuar
    .log("Los 3 destinos han terminado")

    .wireTap("uri:destino") // No espera a que el destino termine. Continúa inmediatamente
    .log("El mensaje ha sido enviado al destino")

> Trabajo en paralelo dentro de una ruta
    multicast().parallelProcessing()
        .to("uri:destino1", "uri:destino2", "uri:destino3")
    .end()   // Los to() se lanzan en paralelo, pero espera a que los 3 destinos hayan terminado para continuar
    .log("Los 3 destinos han terminado")

> En caso de los processors, podemos capturar sus Excepciones con un 
    .transacted()
    .onException(Exception.class)
        .log("Se ha producido una excepción: ${exception.message}")
        .handled(true) // Con esto le digo que la excepción ha sido resuelta
        .handled(false) // Con esto le digo que la excepción aunque le he dado un tratamiento, no está resuelta
        .markRollbackOnly() // Si estamos en una transacción, marca la transacción para que se haga rollback
    .end()

 Al enviar a destinos, si hay una excepción, la exchange se para o no , dependiendo del handled
    .doTry()
        .to("uri:destino1")
    .endDoTry()
    .doCatch( Exception.class )
        .log("Se ha producido una excepción: ${exception.message}")
        .handled(true) // Con esto le digo que la excepción ha sido resuelta
        .markRollbackOnly() // Si estamos en una transacción, marca la transacción para que se haga rollback
    .end()

>  SEDA. Seda es una cola de almacenamiento de datos en memoria
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


> Bucles
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


---

A partir de aquí...Esto que hemos visto nosotros es solo la punta del iceberg.
Es la sintaxis básica y la arquitectura básica de Camel.

En Camel hay ... más de 300 componentes (orígenes y destinos)
Y cada uno de ellos es un mundo... y tiene su propia configuración y sus propias opciones.

La forma guay de montar un proyecto Camel es esta. Esto nos da lugar a código muy mantenible y muy legible y muy reutilizable.

Lo que es la RUTA en SI: lo que hacemos con el configure del RouteBuilder, 
podemos hacerlas en JAVA, pero también en XML y en YAML.
Lo del XML está ya anticuado de narices. Si acaso quiero algo, lo haría en YAML.

Para procesar esas definiciones de rutas en XML o YAML, necesito componentes especiales en el proyecto (libreías en el pom.xml)
YAML: camel-yaml-dsl.

En esos ficheros, lo único que defino es la ruta.
Todo lo que usemos en esa ruta (processors, beans, filtros, etc) los tenemos que definir en JAVA / lenguaje simple de Camel.
En la realidad, fuera de un curso... los procesamientos que querremos meter a los mensajes, serán complejos y el lenguaje simple de Camel se queda corto. Quizás para un filtro:
     mira el header/property X y si es igual a Y, pasa el filtro
    
Al final nos toca escribir esos procesamientos en JAVA.


El problema en cualquier caso es dar con el componente que necesitamos.... y saber como se usa y como configurarlo correctamente.
- Para eso está la documentación oficial de Apache Camel.. ESTO ES UN PURO! aunque es la fuente oficial
  Si quiero afinar mucho en un parámetro de un componente, tengo que ir a la documentación oficial
- Hay otras opciones: Usar un IDE que nos a crear estas rutas.
  KAOTO


---

# Kaoto:

Es una herramienta visual para crear rutas Camel.

```java
//Patron builder
        PersonaIn personaIn = PersonaInImpl.builder()
                                    .DNI(dni)
                                    .nombre("Nombre Apellido")
                                    .fechaDeNacimiento(java.time.LocalDate.of(1990, 1, 1))
                                    .direccion("Calle Falsa 123")
                                    .poblacion("Ciudad")
                                .build();
```

---

KAFKA: Sistema de mensajería distribuido y con persistencia (Diferencia grande con respecto a SEDA)

    Cluster de servidores Kafka
        Maestros: Zookeeper 1
                  Zookeeper 2
                  Zookeeper 3
        Broker 1
        Broker 2
        Broker 3
        ...

Kafka es un sistema de mensajería pull.
Por ejemplo, Rabbit es un sistema de mensajería push.

         llamada telefónica
    APP1 ------------------> APP2

El establecer comunicaciones de punto a punto tiene un problema:
- Si la APP2 no está disponible, la APP1 no puede enviarle los mensajes.

La forma de evitar esto es usar un intermediario.

                Son como el WhatsApp 
                de las aplicaciones

                               pull
                              <-----
    APP1 -----> INTERMEDIARIO -----> APP2
                    KAFKA      push
                    SEDA
                    RABBIT
                    ACTIVEMQ
                    ...


En Kafka los mensajes los vamos dejando en Topics... son como los grupos de WhatsApp

    En un tópico pueden escribir varios productores
    De un tópico pueden leer varios grupos de consumidores
        Dentro de un grupo de consumidores, puedo tener varios consumidores leyendo en paralelo

Un mensaje de un tópico se guarda no solamente en un broker, sino que se replica en varios brokers.
A nivel del tópico definimos el número de particiones y el factor de replicación.

Este tipo de mecanismos los usamos tanto para comunicaciones SINCRONAS como ASINCRONAS.

Lo que garantiza usar una herramienta como Kafka es la entrega del mensaje.


    APP1 -----> KAFKA -----> APP2
             (ASINCRONA) App1 no espera a que APP2 reciba el mensaje ---> FIRE AND FORGET
             (SINCRONA)  App1 espera a que APP2 reciba el mensaje y le conteste ---> REQUEST-REPLY

Un mensaje que dejamos en un típico de Kafka, tiene 2 partes:
- La cabecera (headers): Metadatos del mensaje
- El cuerpo (body): El mensaje en sí

Pues esto encaja perfectamente con Camel.
Camel tiene un componente para Kafka.
    .from("kafka:topic1?brokers=localhost:9092")
    .log("Mensaje recibido de Kafka: ${body}")
    .to("kafka:topic2?brokers=localhost:9092")



---

Las varioables de Camel 4

En lenguaje Simple, podemos usar variables con la sintaxis let

.choice()
    // Usamos 'let' para crear una variable temporal 'precioConDescuento'
    .when(simple("let descuento = ${body} * 0.1; " +
                   "let precioFinal = ${body} - descuento; " +
                   "${body} > 100 && ${exchangeProperty.clienteEsVip} == true"))
        .log("Cliente VIP con precio > 100. Aplicando descuento. Precio final: ${simple:precioFinal}")
        .setBody(simple("${simple:precioFinal}"))
    .otherwise()
        .log("No se aplica descuento.")
.end();

Similar a los properties del exchange.
La única diferencia es que tienen un scope limitado al bloque donde se definen.


Los properties viajan a lo largo de todo el exchange
Cuando tengo rutas complejas, donde hay muchas subrutas, subcomponentes, no puedo garantizar que otros componentes no usen el mismo nombre de property. Ahí puede haber solapamiento y efectos indeseados