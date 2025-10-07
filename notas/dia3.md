
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