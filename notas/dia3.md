
# Camel

Es un framework de integración ... Se basa en el concepto de **Enterprise Integration Patterns** (EIP).
Básicamente la idea es definir flujos de integración entre sistemas, aplicaciones, servicios, etc. ya conocidos y probados.
Esa definición la haremos de forma declarativa.

# Objetivos:

- Facilitar la integración entre sistemas heterogéneos.
- Proveer un lenguaje sencillo y declarativo para definir flujos de integración (rutas).
- Nos ofrece más de 300 componentes para conectar con diferentes sistemas (bases de datos, colas de mensajes, servicios web, archivos, etc.)
- Fácil el procesar / transformar datos/mensajes.
- Capacidades amplias de enrutamiento: Condicional, basado en contenido, etc.


# Arquitectura básica de una ruta Camel

    FROM --> [Procesadores/Transformadores/Enrutadores] --> TO

Lo que mandamos por esas rutas son mensajes (objetos Java: de tipo Message)

Un message tiene dos partes principales que siempre encontraremos:
- Headers: Metadatos del mensaje (parecido a las cabeceras HTTP) CLAVE/VALOR
- Body: El contenido del mensaje (puede ser un objeto Java, un JSON, un XML, etc.)

Opcionalmente puede tener attachments (archivos adjuntos)

# Exchange (Intercambio)

Un Exchange es el contenedor que envuelve a un mensaje (Message) y que además contiene información adicional sobre el intercambio de mensajes.
Un Exchange tiene:
- Un mensaje de entrada (In Message): El mensaje que entra en la ruta.
- Un mensaje de salida (Out Message): El mensaje que sale de la ruta (opcional).
- Propiedades (Properties): Información adicional sobre el intercambio (se parece a los headers pero no se envía con el mensaje... son para procesamiento interno, por ejemplo para pasar información entre procesadores dentro de una ruta).
- Excepción (Exception): Si ocurre un error durante el procesamiento del mensaje, la excepción se almacena aquí.

```java
// Camel me ofrece un objeto Exchange en los procesadores
Exchange exchange = ...; // Obtenido de algún lugar, por ejemplo en un procesador
Message inMessage = exchange.getIn(); // Obtener el mensaje de entrada
String body = inMessage.getBody(String.class); // Obtener el cuerpo del mensaje como String
Map<String, Object> headers = inMessage.getHeaders(); // Obtener los headers del mensaje

// Si quiero modificar el mensaje de salida
Message outMessage = exchange.getOut();
outMessage.setBody("Nuevo cuerpo del mensaje");
outMessage.setHeader("NuevoHeader", "Valor");

// Si ocurre un error
Exception exception = exchange.getException();
if (exception != null) {
    // Manejar la excepción
}
```

En paralelo a todo esto, Camel me da un Context (CamelContext) que es el contenedor principal de Camel.

Dentro de ese contexto es donde configuro las rutas, los componentes, configuración global, capacidades de monitorización, etc.


# Sintaxis básica de los from... to, e incluso los procesadores

```java
from("componente:configuracion") # Lo que añadimos dentro es una URI
    .to("componente:configuracion");
```

En esa URI siempre empezamos con el nombre del componente (file, ftp, jms, http, etc.) seguido de dos puntos y la configuración específica de ese componente.
Lo que marquemos como protocolo (antes de los dos puntos) es lo que nos dice qué componente usar.
No confundamos el componente con el protocolo... El componente es código que sabe cómo manejar ese protocolo.

    http:// --> Componente HTTP
    file:// --> Componente File
    timer:// --> Componente Timer

    Lo que entendemos en estas URIs como protocolo es lo que le dice a Camel qué componente usar.

Además hay operaciones básicas de transformación y procesamiento

```java
// Transformación del cuerpo del mensaje
.transform(body().prepend("Hola "))
.transform(body().append("!!!"))
.setBody(constant("Mensaje constante"))
.setHeader("MiHeader", constant("ValorHeader"))
.setHeader("OtroHeader", simple("${date:now:yyyy-MM-dd}")) // Usando lenguaje Simple
.removeHeader("MiHeader")
```
En muchos casos, necesitaré tirar de lógica más compleja... para eso están los procesadores (Processor)

```java
// Sintaxis Orientada a Objetos
.process(new Processor() {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.toUpperCase();
        exchange.getIn().setBody(body);
    }
})

// Sintaxis con Expresiones Lambda (Java 8+)
.process(exchange -> {
    String body = exchange.getIn().getBody(String.class);
    body = body.toUpperCase();
    exchange.getIn().setBody(body);
})

// En Spring... la cosita se puede complicar un poco más
@Component
public class MiProcesador implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.toUpperCase();
        exchange.getIn().setBody(body);
    }
}   

// Y luego en la ruta
.process("miProcesador") // Referenciando el bean de Spring por su nombre
```

# simple() : Lenguaje Simple de Camel

Lenguaje de plantillas para acceder a datos del mensaje, propiedades del sistema, fechas, etc.

```java
// Sintaxis básica:
simple("${expresion}")

// Acceder a datos del mensaje
simple("${body}") // Cuerpo del mensaje
simple("${header.NombreHeader}") // Valor de un header específico
simple("${headers}") // Todos los headers
simple("${property.NombrePropiedad}") // Valor de una propiedad del exchange
simple("${properties}") // Todas las propiedades del exchange
simple("${exception.message}") // Mensaje de la excepción si existe

// Funciones de fecha y hora
simple("${date:now:yyyy-MM-dd}") // Fecha actual en formato específico
simple("${date:header.NombreHeader:yyyy-MM-dd}") // Fecha en un header específico

// Podemos usar operadores lógicos y condicionales, funciones estándar de Java
simple("${body} contains 'Hola'") // Verifica si el cuerpo contiene 'Hola'
simple("${header.NombreHeader} == 'Valor'") // Compara el valor de un header
simple("${body.toUpperCase()}") // Convierte el cuerpo a mayúsculas
simple("${body.length()}") // Longitud del cuerpo del mensaje
// Operaciones matemáticas
simple("${header.Numero1 + header.Numero2 % 2 }") // Suma de dos headers numéricos

simple("${exchangeId}") // ID del exchange
simple("${routeId}") // ID de la ruta
simple("${camelId}") // ID del contexto Camel
simple("${hostname}") // Nombre del host donde se ejecuta Camel
```

Tendemos a nuestra disposición funciones de enrutamiento (choice, when, otherwise, etc.), de agregación (aggregate, completionSize, etc.), de split (split, tokenize, etc.), y muchas más.

```java
// Ejemplo de enrutamiento condicional
from("...")
    .choice()
        .when(simple("${header.NombreHeader} == 'Valor'"))
            .to("direct:valor")
        .otherwise()
            .to("direct:default")
    .end();

// Filtros
from("...")
    .filter(simple("${body} contains 'Importante'"))
    .to("direct:importante");


---
Una de las gracias de respetar el principio de inversión de dependencias es la facilidad para hacer testing.

Los desarrolladores en general sabemos muy poco del mundo del testing.

FABRICACION DE BICICLETAS? 

- BTWIN : Decathlon
   Ruedas las fabrico yo? No
   Cuadro? No
   Manillar? No
   Sistema de frenos? No
   Sillín? No

   Y qué cojones pinto yo? Diseño, encargo o busco componentes y los integro.

   Perfecto. Me mandan el sistema de frenado. Lo he comprado a Shimano. 
   Que hago lo primero? LE HAGO PRUEBAS A ESE COMPONENTE ***AISLADO***
    Probar el sistema cuando lo tenga montado tiene 2 problemones GORDISIMOS:
    - Y si luego no? A desmontar? Y además... dónde está el problema? Por qué no funcionan?
    - Y hasta que no tenga TODO (LA BICI ENTERA) no tengo npi de cómo voy?
    - El tiempo que he invertido en montarlo...

    Pruebas UNITARIAS: Son pruebas que hago de un componente AISLADO!
    Cómo pruebo el sistema de frenos... de forma aislada?
    - Lo monto en un bastidor (4 hierros mal soldaos... que aguanten para la prueba... que confíe en ellos)
    - Cómo lo pruebo? Qué acción ejecuto? sistemaFrenos.aprietoPalanca();
    - Resultado esperado? Que las pinzas de freno cierren... y a lo mejor con una cantidad determina de presión.
                          Monto un sensor de presión entre las pinzas.

                          SENSOR & BASTIDOR = TEST DOUBLE (Mocks, Stubs, Fakes, Spies, Dummies)
    
    Me llega el sillín, le hago pruebas unitarias.
        - Carga     \
        - Estrés     > NO FUNCIONALES
        - UX        / 
        Las pruebas unitarias, igual que las de integración o las de sistema, pueden ser funcionales o no funcionales.
    Me llegan las ruedas: Le hago pruebas unitarias. La monto en un bastidor, le pego un viaje con la mano y a ver si gira... 

    Me garantizan estas pruebas que la bici va a funcionar? NO
    Que gano haciéndolas? CONFIANZA +1 Voy bien! Voy dando pasos en firme!

Ya he hecho pruebas unitarias a todos los componentes. Me tocan las pruebas de Integración.
Para ello junto componentes 2 a 2. El objetivo de la prueba de integración es verificar la comunicación entre componentes.
    - Ruedas + Sistema de Frenos.
    Cómo pruebo el conjunto sistema de frenos/ruedas?
    - Monto ambas en el bastidor... con la rueda entre las pinzas de freno.
    - Le pego un viaje a la rueda
    - Y ejecuto: sistemaFrenos.aprietoPalanca();
    - Resultado esperado: Que las pinzas cierren? NO.. porque ya lo sé!
                          Que cierran con fuerza? NO... porque ya lo sé!
                          Que la rueda se para!
                          Y MIRA QUE NO!
                          Y cuando miro por qué, resuelta que las pinzas cierran... con fuerza... pero no lo suficiente como para llegar a tocar la llanta de la rueda. Es muy estrecha.
                          Está mal el sistema de frenos? Funciona defectuoso? NO
                          Está mal la rueda? Funciona defectuosa? NO
                          Tengo un problema en la comunicación entre ambos. El sistema de frenos no es capaz de comunicar la energía de rozamiento suficiente a las ruedas para que frenen.
                          DECISION! Cambiar la rueda... Pero ojo... la nueva ya no entra en cuadro que tenia encargado.... hay que hacer cambios en el cuadro.
                          Menos mal que el cuadro aun ni lo tengo... y he hecho estas pruebas antes de que monten el cuadro!

    Me garantizan estas pruebas que la bici va a funcionar? NO
    Que gano haciéndolas? CONFIANZA +1 Voy bien! Voy dando pasos en firme!

    Y cuando ya he hecho todas estas pruebas paso a las pruebas de sistema... que se centran en el comportamiento del sistema en su conjunto.
    Monto la bici, doy pedales y va pa`tras! Tengo un problema....

    Imaginad que si.. Cojo a un tio. Le monto en la bici.. Bocadillo de chorizo y botella de agua en la mochina.. A cuenca!
    A las 4 horas vuelve.. Sano y salvo... con el culo comodito!
    Las pruebas de sistema funcionan bien.
    YA PUEDO ENTREGAR? SI!

    Pregunta!
    Si en lugar de haber hecho tanto rollo.. hubiera hecho directamente estas pruebas y funcionan bien... necesitaría hacer pruebas unitarias y de integración? NO...
    Entonces... donde está el truco? El truco está en 2 puntos de esa frase:
    - Si funcionan bien... y si no? donde está el problema? NPI .. ponte a averiguar!
    - Cuando puedo hacer estas pruebas? Cuanto tengo la bici completa... y hasta entonces? VOY A CIEGAS?

--- 

Como aplicamos todo esto a nuestro proyecto CAMEL... y como nos ayuda SPRING en ello!

En una ruta qué tenemos?
    FROM
    PROCESSORS 
    FILTERS
    ROUTING (Condicionales)
    TO

Son muchas cositas... que hay que poner juntas... a ver si funcionan...
Pero... las pruebo cuando estén todas juntas? SI (Sistema)
Pero antes, querré posiblemente probar cada cosa por separado!
Una vez que cada cosa funcione por separado, que hago?
- Pruebo cada cosa con la de antes y la de después (Integración)

Y si es así, si funcionan bien juntas 2 a 2... pues ya meto todo junto y pruebo el sistema.

Ahora, como pruebo el procesador1?
De donde le mando los datos para que trabaje? Lo conecto ya a la BBDD? o al aplicativo X? NO
Genero un TEST DOUBLE (Mock, Stub, Fake, Spy, Dummy) alguien que pueda mandar datos de mentirijilla a mi procesador1... simulando que es la bbdd o applicativo X.

Y miro que lo que genera ese procesador sea lo que se espera de él!

Pregunta... nuestro procesador (EL BACON IPSON LOREM CHORIZUM!!!!) está conectado con alguien? INTIMAMENTE CONECTADO CON ALGUIEN?
Tiene dependencias con alguna otra clase de mi proyecto.. algún otro componente?


Cómo sé si un programa funciona... quién es el responsable último.. quien dice esto?

Quién dice que un programa funciona? 
    - EL USUARIO! Al usuario le tiene que llegar un sistema que funcione totalmente.
    - LAS PRUEBAS son las que dicen si un programa funciona o no.
    - Las pruebas se hacen para comprobar unos requisitos funcionales y no funcionales.
    - Los usuarios podrán ayudarme a definir algunos requisitos funcionales y no funcionales.