
En un proyecto Camel, siempre configuramos RUTAS.
Vamos a crear una carpeta para ellas.

Además, esas rutas siempre tienen la misma pinta:

    FROM -> (procesos intermedios) -> TO

La idea es que los FROM y los TOs son elementos TONTOS... Pero tontos de verdad.
Solo saben recibir o enviar mensajes/datos.
Sacar datos de una cola de mensajería Kafka, o de un fichero, o de una base de datos.
- Cambia de un proyecto a otro la forma de sacar un dato o de escribirlo a KAFKA?         NO
- Cambia de un proyecto a otro la forma de leer o escribir un fichero?                    NO
- Cambia de un proyecto a otro la forma de insertar un dato en una tabla de una BBDD SQL? NO

Esas cosas necesitan una configuración.,.. si acaso!
- Por ejemplo, si quiero leer ficheros.. le diré de que carpeta los lea.
- Si quiero leer de una cola Kafka, le diré la URL del servidor Kafka, la cola (Topic) que quiero leer, etc.
- Si quiero leer de una BBDD SQL, le diré la URL del servidor, el usuario, la contraseña, etc.... y la tabla.

La idea es que esa parametrización se suministre a Camel en las `uris`que usamos en los FROM y los TOs.

Y esos parámetros, a su vez, nosotros los vamos a poner en un fichero de configuración... que bueno que Spring Boot me regala uno: `application.yaml` o `application.properties`

Dónde está la lógica GORDA de procesamiento de datos? En esos procesos intermedios.
Que en el mundo Camel son ejecutados por Procesadores (Processor).
Esos procesors son los que he de crear, programar, con sumo cuidado... son los que llevarán lógica pesada de negocio.
Y por supuesto, a los que querré hacer pruebas intensivas. 
Lo primero que me tendré que asegurar es que esos processors funcionan bien por si mismos, Aislados de otros processors y de from y to.
Luego ya los integraré en las rutas Camel.... y también lo probaré la ruta completa (Pruebas de sistema-end-to-end). Aun qué antes de probar la ruta completa, me interesará ir juntando varios processors y probándolos juntos (Pruebas de integración).

Esos processors vamos a meterlos en su propia carpeta.

> Siguiente!

Por la ruta qué viaja?
    El Exchange es la información de un viaje que se está haciendo por la ruta.
    Pero lo que viaja qué es? DATOS/MENSAJE.

    Y esos Datos/Mensaje, en qué formato están? ME LA PELA !!!!!!
    Y Aquí hay que hacer una diferencia muy grande entre lo que es:
    - DATO                          MODELO!
    - REPRESENTACIÓN DEL DATO


Una persona es una persona... no es un json, ni un xml, ni un yaml. La persona es el modelo.
Y una persona tiene:
- Id            1
- Nombre        "Menchu"
- Edad          30  
- Dirección     "Calle Falsa 123"
- Teléfono      "123456789"
- Email        "menchu@ejemplo.com"
- etc       

Otra cosa es cómo vamos a representar esa persona en un fichero o en un mensaje.
Esa persona (Menchu) la podemos representar en:
```json
{
  "id": 1,
  "nombre": "Menchu",
  "edad": 30,
  "direccion": "Calle Falsa 123",
  "telefono": "123456789",
  "email": "menchu@ejemplo.com"
}
```
O en XML:
```xml
<persona>
  <id>1</id>
  <nombre>Menchu</nombre>
  <edad>30</edad>
  <direccion>Calle Falsa 123</direccion>
  <telefono>123456789</telefono>
  <email>menchu@ejemplo.com</email>
</persona>
```
O en YAML
```yaml
persona:
  id: 1
  nombre: Menchu
  fechaDeNacimiento: 1993-04-15
  direccion: Calle Falsa 123
  Poblacion: Ciudad Ficticia
  CP: 28080
  Pais: España
  telefono: 123456789
  email: menchu@ejemplo.com
```
O la puedo tener dentro de una BBDD Oracle, en una tabla... como un registro.

El dato es el dato, el dónde esté el dato o su representación es anecdótico.

El punto es que nosotros leeremos/recibiremos de un origen (FROM) un MODELO (Unos datos) en una REPRESENTACIÓN (JSON, XML, YAML, BBDD, etc).
Y nosotros tenemos que transformar esos Datos en otros Datos (Otro Modelo), que cuando lo 
enviemos/guardemos en otro sistema, en él tendrán otra Representación (JSON, XML, YAML, BBDD, etc).


          ORIGEN                                                       DESTINO
    Datos + Representación -> Datos -> Proceso -> Otros Datos -> Otros Datos + Representación
                              MODELO   PROCESSORS   MODELO
        ---------------------------------------------------------------> 
                                    RUTA CAMEL


En ocasiones, necesitaremos convertir unos modelos en otros modelos.... y para ello podremos usar 2 cosas:
- Si la Conversión es muy simple... nos sirve lo que llamamos un Mapper (Mapeador)
  Un Mapper es una clase que tiene un método que recibe un modelo y devuelve otro modelo.
  Y punto. Y EN JAVA TENEMOS EXCELENTES MAPEADORES COMO MAPSTRUCT.
- Si la Conversión es compleja... necesitaremos un Processor (Procesador)
  En este caso, de hecho, lo más habitual va a ser seguir usando dentro del Processor un Mapper.
  Pero esos mapeadores... ya son específicos de una implementación concreta.


# Para que sirven los getters y setters en JAVA

> Para modificar las propiedades de un objeto o recuperarlas... FALSO

```java

public class Persona {
    public String nombre;
    public int edad;
}

Persona p = new Persona();
p.nombre = "Menchu";
p.edad = 30;
System.out.println(p.nombre + " tiene " + p.edad + " años");
```

Claro... que si hago esto, me cortan las manos... ES UNA MUY MUY MUY MALA PRÁCTICA en JAVA.... por la mierda de cómo está hecho JAVA.

> Me venden que es para conseguir encapsulamiento... EIN??? 
Que yo tenga control de mis variables y que nadie pueda tocarlas directamente... PODRIA SER...
Pero, al final en el 99,99% de los casos, los getters y setters son públicos...y planos:

```java
public class Persona {
    private String nombre;
    private int edad;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}

Persona p = new Persona();
p.setNombre("Menchu");
p.setEdad(30);
System.out.println(p.getNombre() + " tiene " + p.getEdad() + " años

```
Esto me dicen que es la buena práctica en JAVA... y es cierto... pero es una mierda... por carencias que tiene Java en su sintaxis.

> Entonces.. debería poder elegir si quiero / necesito ese encapsulamiento o no... y si no, poder dejar las variables como públicas...

Cuál es el problema de eso?


```java
// DIA 1
public class Persona {
    public String nombre;
    public int edad;
}
// DIA 2-100 
Persona p = new Persona();
p.nombre = "Menchu";
p.edad = 30;
System.out.println(p.nombre + " tiene " + p.edad + " años");
// DIA 101..y al levantarme de la cama.. me digo! YA LO TENGO... voy a hacer que las edades no puedan ser negativas
// Tengo que poner un if! Dónde? NO HAY SITIO... Y me toca la variable edad hacerla privada y generar getters y setters
// para tener un sitio triste donde poner el if.
public class Persona {
    public String nombre;
    private int edad;

    public int getEdad() {
        return edad;
    }
    public void setEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.edad = edad;
    }
}
// DIA 102... qué pasa?
Pasa, que tengo 300k personas, kalasnikovs en mao, persiguiendome por todo el mundo. Acabo de joder el código de 300k personas, que durante 100 días han estado usando la variable edad directamente... y su código ni compila.
```

Y entonces, me dicen en JAVA, COMO SOY UNA MIERDA DE LENGUAHE QUE NO TENGO NADA INTELIGENTE PARA QUE EL DIA DE MAÑANA PUEDAS PONER EN ALGUN SITIO UN PUÑETERO IF, por si acaso se te ocurre.. que no lo sé .. pero por si acaso, escribe 10 lineasa de código más hoy... estúpidas, que no aportan nada.. más que dejar un hueco preparado, por si el dia de mañana se te ocurre poner un if.

Y ESTA ES LA VERDADERA RAZÓN DE SER DE LOS GETTERS Y SETTERS EN JAVA > FACILITAR LA MANTENIBILIDAD DEL CÓDIGO.

```kt
public class Persona(var nombre: String, var edad: Int)
```

La gente dice.. joder con JAVA ... es muy verboso... YA VES TU !!!!! Lo que es es un desastre de lenguaje.

BASTANTE DESGRACIA TENEMOS con la necesidad de usar getters y setters en JAVA... como para encima TENERLOS QUE ESCRIBIR...
Ya por suerte, desde hace muchos años, los getters y setter, voy a pedir que los escriban por mi... LOMBOK

Es una librería de JAVA que mediante anotaciones, me genera el código repetitivo que no quiero escribir.
El problema ya no es generar el código.. que muchos IDEs o las IAs lo hacen por mi.. El problema es que no quiero tener 3 pantallas de getters y setters en mis clases... y yo como un pendejo haciendo scroll para ver el código que me interesa.  


MAPEO es cambiar de una estructura de datos (o varias) a otra estructura de datos (o varias).
Y eso se hace con MAPEADORES (MAPPERS).
Otra cosa son de dónde salen esos datos / Como los calculo: PROCESADORES (PROCESSORS).
Y Otra la representación de esos datos: JSON, XML, YAML, BBDD, etc.
Y cómo los transformo a esa representación: FORMATEADORES (MARSHALLERS/UNMARSHALLERS).
    Como por ejemplo es Jackson.


    XML                 -> XSLT                     -> XML
    DATO+REPRESENTACIÓN -> PROGRAMA                 -> DATO+REPRESENTACIÓN


SOLID: % Grandes principios de diseño de software orientado a objetos
- S: Single Responsibility Principle (SRP) Principio de responsabilidad única           <<<<<

    Hay gente que lo confunde con el Soc: Principio de segregación de responsabilidades/preocupaciones
    Pero no es lo mismo.

    El SRP nos dice que un componente no debería tener funciones / trabajo sobre el que puedan tomar decisiones más de una actor que tenga responsabilidad en el producto.

- O: Open/Closed Principle (OCP) Principio de abierto/cerrado
- L: Liskov Substitution Principle (LSP) Principio de sustitución de
- I: Interface Segregation Principle (ISP) Principio de segregación de interfaces
- D: Dependency Inversion Principle (DIP) Principio de inversión de dependencias        <<<<<



HERRAMIENTAS + LENGUAJES + FRAMEWORKS + METODOLOGÍAS + ARQUITECTURAS + BUENAS PRÁCTICAS + PATRONES DE DISEÑO
MAVEN           JAVA 21     Spring/Camel      Agil        Componentes   SRP, DIP         
                que es muy distinto de JAVA 8, 11
                                            5, 7


Todas esas cosas, evolucionan en paralelo, para resolver los nuevos problemas que se nos van presentando.
Si intento tomar un item de esa lista y aplicar a otro conjunto de items de la lista, NO ENCAJA.

Para trabajar como lo hacíamos hace 20 años, con XML, XSLT, EJ en una arquitectura monolítica, Spring no encaja.

                                 PersonaIn                      PersonaOut / XML
Vamos a sacar los datos de una BBDD SQL y a enviarlos a una cola Kafka.
Vamos a sacar los datos de una cola KAFKA XML y a enviarlos a un fichero JSON.
Vamos a sacar los datos de un fichero JSON y a enviarlos a un servicio REST.


Tenemos varias cosas:           
- Extracción de los datos de un origen (FROM) BBDD ? Algo que hacer aquí?     ALGO... no mucho 4 parámetros (y 6 líneas de código)
- Lo gordo... procesamiento de los datos (PROCESS) ? Aquí hay que currar... MUCHO
- Volcado de datos a un destino (TO) Kafka ? Algo que hacer aquí?      POCO... no mucho 4 parámetros (y 6 líneas de código)


RUTA 1: 
    FROM BBDD SQL ->        PROCESS -> TO KAFKA XML
    Datos en binario -> JPA -> PersonaIn ->  PROCESS -> PersonaOut ->  marshaller --> XML -> KAFKA
                    unmarshaller


    J2EE -> Java Enterprise Edition -> JEE -> Jakarta EE
    Es una colección de especificaciones para hacer aplicaciones empresariales en JAVA.
    JPA es una especificación de JEE para gestionar automáticamente el mapeo de objetos JAVA a tablas de BBDD SQL.


PROCESOS RUTA 1
    Calcular la edad a partir de la fecha de nacimiento




    Fecha de Nacimiento -> Edad     No es un simple cambio de formato (marshaller/unmarshaller)
                                    Es un cálculo que implica una lógica de negocio.
                                       Redondeo hacia arriba.. redondeo hacia abajo... etc
                                       Redondeo al más cercano... quieres tener en cuenta años bisiestos?
                                       O una base de año de 360 días?

                                       Aquí hay decisiones, que yo tomo (LOGICA DE NEGOCIO)

    Validar el DNI                     Lógica de negocio

    Filtro                             Solo datos que tengan un DNI válido
                                       Solo datos que tengan un email válido

Tengo BBDD? NO... me impide ponerme a trabajar? NO
Tengo KAFKA? NO... me impide ponerme a trabajar? NO