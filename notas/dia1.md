
# CAMEL

Framework JAVA para desarrollo de integraciones entre sistemas.
Además, hoy en día, lo habitual es usar CAMEL desde SpringBoot.

# Springboot (+200 librerías)

Es otro framework JAVA. El gran framework de desarrollo de aplicaciones en JAVA.
Es un contenedor de inversión de control (IoC).

# Librerías

Al montar una de nuestras aplicaciones, de integración con Camel,
necesitaremos muchas librerías... Librerías de Camel, SpringBoot, y más librerías (>300)

La gestión de esas librerías es una locura. Absoluta! Para ayudarnos con ella, y con otras tareas de nuestro proyecto: Compilación, ejecución, pruebas... vamos a usar una herramienta que existe en el mundo JAVA: MAVEN.

# MAVEN

Es una herramienta de automatización de tareas en proyectos de desarrollo de software... principalmente en proyectos JAVA.

Maven nos va a ayudar a descargar las librerías que necesitemos. Para ello, buscará esas librerías en algún sitio (un registro de librerías) y las descargará en nuestra máquina.

En el banco usáis ARTIFACTORY, que es un registro privado de librerías.
Una aplicación (BBDD) de librerías, que el banco va colocando allí para que podáis descargarlas y usarlas.

El banco controla los sitios a los que accedéis para descargar librerías.... mediante un proxy.

En maven, tenemos que configurar el proxy (URL, usuario, contraseña)

# VSCODE: 

Lo vamos a usar como entorno de desarrollo (si bien, no es un IDE específico de JAVA).
Conseguimos que nos eche una mano en proyectos java, instalándole extensiones.
Para instalar esas extensiones (plugins) necesitamos configurar el proxy dentro de VSCODE.

---

# Framework vs Librería

Librería trae código, funciones que nosotros usamos en nuestros desarrollos.
Framework es un conjunto de librerías, que además imponen una forma concreta de trabajo.

---

# MAVEN

Maven es una herramienta de automatización de tareas en proyectos de desarrollo de software... principalmente en proyectos JAVA.

JAVA es un lenguaje de programación.


## Lenguajes de tipado estático (fuerte) vs dinámico (débil)

Todos los lenguajes de programación manejan datos.
Y esos datos serán de un determinado tipo: Números, cadenas de texto, fechas, booleanos...

Además, en todo lenguaje de programación tenemos el concepto de VARIABLE.
El concepto de variable cambia de lenguaje a lenguaje:
- En C, C++, Fortrán, ADA... es un lugar en memoria RAM donde albergamos un dato.
- En Python, Javascript, JAVA una variable es otra cosa.
  En estos lenguajes, una variable es una referencia a un dato en memoria. Tiene más que ver con el concepto de PUNTERO en C/C++.

```java
        // ->
String texto = "hola"; // Asignamos "hola" a la variable texto
        // "hola"  -> Crea un objeto de tipo String en memoria RAM, con valor "hola"
        // String texto -> Crea una variable de tipo String con nombre "texto"
        // =  -> Asigna la variable "texto" al dato "hola"
        // Pego el postit en el cuaderno... al lado de donde pone "hola"
texto = "adios";
        // "adios"  -> Crea un objeto de tipo String en memoria RAM, con valor "adios"
            // Donde? En el mismo sitio donde estaba "hola" o en otro sitio? EN UNO NUEVO
            // Y llegados a este punto en la RAM tenemos dos objetos String: "hola" y "adios"
        // texto =  -> Despegamos el postit de donde estaba, y lo pegamos al lado de "adios"
        // Reasignamos la variable. Lo que cambia (lo que varia) es la variable, no el dato.
            // Y llegados a este punto, cuántas variables tengo apuntando al dato "hola": 0
            // Y por eso, ese dato es marcado como "BASURA": GARBAGE... Y quizás i o quizás no, el GARBAGE COLLECTOR (GC) de la JVM lo borre. No hay control de esto.
```
En JAVA, las variables también tienen TIPO DE DATO.
En Python o en JS no es así. Es como si todos los postit fueran iguales pudieran apuntar a datos de cualquier tipo.

Cuando el lenguaje me obliga a definir tipos de datos para las variables, hablamos de lenguajes de TIPADO ESTÁTICO (fuerte).
Cuando el lenguaje no asocia tipos de datos a las variables, hablamos de lenguajes de TIPADO DINÁMICO (débil).

```java
var texto = "hola"; // Válido en JAVA desde la v.11
texto = 3; // Error de compilación. No puedo asignar un número a una variable que ya tiene asignado un String, ya que la variable "texto" sigue siendo de tipo String
```
Ahora bien... entonces, JAVA soporta tipado dinámico? NO
en JAVA lo que se implementó fue INFERENCIA DE TIPOS.
El tipo de una variable, si no lo fuerzo explicitamente, se infiere del valor que le asigno en la primera asignación.

# Compilados vs interpretados

Los computadores entienden JAVA? NO y C? Tampoco
0 y 1? Nasti...
Los computadores entienden de estados duales:
- Llega corriente por esta patilla del microprocesador en este momento del tiempo o no?
- Esta magnetizado este trozo de disco duro en este momento del tiempo o no?
Habitualmente los humanos representamos esos estados duales con 0 y 1.... para entendernos... es buena idea.
Pero entonces cómo me comunico con la computadora?
Quién se comunica con los componentes físicos de la computadora es el SISTEMA OPERATIVO.
Y cada SO tiene su propio lenguaje de comunicación hacia arriba... es decir, la forma en la que YOP. me comunico un Sistema Operativo depende de ese SO.
Los SO tampoco entienden de JAVA, ni de C...

Pero nuestros programas los escribimos en JAVA, C...
Hay que traducirlos al lenguaje del SO.

Ese proceso lo podemos hacer de 2 formas...
- Pre traducción estática (compilación)                         C, C++
- Es tu problema... contrata tu propio intérprete (intérprete)  PY, JS

Java es las 2 cosas a la vez. Primero se compila a un lenguaje intermedio (bytecode), y luego ese bytecode es interpretado por la JVM (Java Virtual Machine).

    .java ---> compilamos ---> .class (bytecode) ---> interpretamos JVM ---> SO
                 ^^^^
                javac
                Cuando tenemos muchas librerías en nuestro programa... es horrible.
                Tenemos que configurar 300 rutas... para que encuentre las librerías.
                PROBLEMILLA !!!!

En el caso de java, en la versión 1.2 se incluyo el HOTSPOT, ddentro del JIT (Just In Time compiler).
HOTSPOT es una caché de código compilado.

Las apps de JAVA necesitan eso si un WARM UP TIME.

## Paradigmas de programación

Un paradigma es un nombre "hortera" que los desarrolladores usamos para definir la forma en la que usamos un lenguaje para comunicar una intención.

- Imperativo            Cuando damos instrucciones(órdenes) que deben procesarse secuencialmente
                        A veces queremos romper la secuencialidad... y para ello usamos las típicas expresiones condicionales (if, switch) y los bucles (for, while)
- Procedural            Es cuando agrupamos instrucciones en grupos de instrucciones bajo un nombre: 
                        - funciones, métodos, procedimientos, subrutinas... Y puedo usarlas posteriormente mediante ese nombre.
                        Ventajas:
                        - Reutilización de código
                        - Mejora la organización del código, lo que facilita su lectura y MANTENIMIENTO
                        - En programación funcional, también para poder pasar lógica a otras funciones
- Funcional             Esto es una rayada. En JAVA se incluyó en la versión 1.8.
                        Cuando el lenguaje me permite que una variable apunte a una función, y posteriormente invocar (ejecutar) esa función a través de la variable, el lenguaje soporta programación funcional.
                        El tema no es lo que es la programación funcional... sino lo que me permite hacer... cómo cambia la escritura de mi código cuando el lenguaje soporta eso:
                        - Podemos crear funciones que reciben otras funciones como parámetros
                        - Podemos crear funciones que devuelven otras funciones
                        Y ESO ES LO QUE NOS EXPLOTA LA CABEZA
- Orientado a objetos   Todo lenguaje viene con una serie de tipos de datos por defecto.
                        Cuando el lenguaje me permite crear mis propios tipos de datos, con sus propiedades y sus operaciones, el lenguaje soporta POO.

                                Propiedades/Características    Operaciones/Métodos
                        Texto   secuencia de caracteres        mayúsculas, minúsculas, trim, split...
                        Fecha   día, mes, año                  caesEnJueves()

                        Coche   marca, modelo, color           arranca(), para(), acelera(), frena()

- Declarativo           Spring, Camel

No es algo propio (o exclusivo) de los lenguajes de programación. Lo tenemos también en los lenguajes naturales (los que usamos para comunicarnos los humanos).

> Felipe, IF(Si) hay algo que no sea una silla en la ventana, CONDICIONAL
>   quítalo.  IMPERATIVO
> Si hay no hay una silla en la ventana, CONDICIONAL
>.   If not silla (silla == False) GOTO IKEA! y compra silla!
> Felipe, pon una silla debajo de la ventana.         IMPERATIVO

Estamos muy acostumbrados a paradigma imperativo. De hecho el 90% del código que escribimos es imperativo (todo lo que va en una función o método) es imperativo.
El lenguaje imperativo me hace olvida lo que quiero conseguir.. para hacerme centrar en cómo conseguirlo.

> Felipe, debajo de la ventana tiene que haber una silla. Es tu responsabilidad. DECLARATIVO
  SOLO le estoy expresando lo que DEBE SER... y lo que es.

Adoramos los lenguajes declarativos... me ayudan a centrarme en lo que quiero conseguir... paso del cómo conseguirlo... eso lo delego!

---

"Un producto de software es por definición un producto sujeto a cambios y mantenimiento"

    ESCRIBO CÓDIGO <> PRUEBAS -> OK -> REFACTORIZAR <> PRUEBAS -> OK -> LISTO!

    <------ 50% del trabajo ------>   <----- 50% del trabajo ------>
            8 horas.                         8 horas.

---

# Maven

Herramienta de automatización de tareas en proyectos de desarrollo de software... principalmente en proyectos JAVA.
Tareas con las que nos ayuda:
- Gestión de dependencias (librerías) (Conseguir las librerías que necesito, mantenerlas actualizadas)
- Compilación
- Ejecución
- Pruebas
- Empaquetado (JAR)

## Estructura de un proyecto al trabajar con maven

proyecto/
|-- src/
|   |-- main/
|   |   |-- java/ <---- Código fuente de la aplicación
|   |   |-- resources/ <---- Recursos (ficheros de configuración)
|   |-- test/
|       |-- java/ <---- Código fuente de las pruebas automáticas
+-- pom.xml
|
+-- target/ <---- Directorio donde maven coloca los ficheros generados (compilados, empaquetados...)
    |-- classes/            <---- Código compilado de la aplicación
    |-- test-classes/       <---- Código compilado de las pruebas automáticas
    +-- proyecto.jar        <---- Fichero empaquetado (JAR) de la aplicación. Esto es lo que se distribuye (se guardará en el artifactory del banco)


## POM.XML

Este es crítico. Iremos a él con frecuencia.
Es un archivo XML que define TODA LA CONFIGURACION para maven de nuestro proyecto.
Dentro de este archivo tendremos:
- Datos identificativos del proyecto:
  - groupId: Eso nos permite agrupar distintos proyectos
  - artifactId: Nombre del proyecto concreto
  - version: Versión del proyecto (a.b.c)
- Metadatos:
  - Nombre del proyecto
  - Quién lo crea
  - URL repo de git
- Dependencias (librerías) que usa el proyecto
- Plugins (herramientas) que usa el proyecto. Maven no sabe hacer la o con un canuto. Todo lo hace mediante plugins.
    - Compilar: Plugin de compilación
    - Empaquetar: Plugin de empaquetado
    - Ejecutar: Plugin de ejecución
    - Pruebas: Plugin de pruebas

    Por defecto, ya vienen muchos plugins preconfigurados en maven... que no es necesario que declaremos en el POM.XML.
    Pero hay algunos que si tendremos que declarar, para tareas muy particulares de nuestro proyecto.


---

# Spring

Spring es un framework con más de 200 librerías JAVA, que ofrece es un contenedor de inversión de control (IoC), y una forma sencilla de utilizar patrones de diseño como inyección de dependencias (DI).

---

> Programa de consola (comando) para buscar palabras en un diccionario.
> El programa debe decirme si la apalabra existe o no... y si existe sus significados.

c:\> buscaPalabra "melón" "ES"
La palabra existe.
Significados:
- Fruto del melonero.
- Persona con pocas luces.

Pregunta: Cuántos proyectos monto para este programa? -> Cuántos repositorios de git monto? 4

- Frontal: Consola / comandos
- API de comunicación entre ellos
- Backend: Lógica de buscar palabras en un diccionarios y sacar los significados
- Datos/Diccionarios.

---

API de diccionario: diccionarios-api.jar

```java
package com.diccionarios;
import java.util.Optional;

public interface Diccionario {
    String getIdioma();
    boolean existe(String palabra);
    Optional<List<String>> getSignificados(String palabra);
    // Palabra: melón: ["Fruto del melonero", "Persona con pocas luces"]
    // Palabra: "archilococo": Lista vacia... null... error
      // Exception: En la santa vida devolvería una excepción en un caso como este: SERÍA UNA MUY MALA PRÁCTICA
              // Generar una Exception es muy caro en términos de recursos de CPU y memoria.
              // Al menos sería explicito:  throws PalabraNoExisteException
      // null | Lista vacia... Pero es ambiguo. En JAVA 1.8 se añade la clase Optional<T> para estos casos.
}

public interface SuministradorDeDiccionarios {
    boolean tienesDiccionarioDe(String idioma);
    Optional<Diccionario> getDiccionarioDe(String idioma);
}
```
---

Backend: diccionario-desde-ficheros.jar

```java
package com.diccionarios.ficheros;
public class DiccionarioSobreFicheros implements Diccionario {
    public boolean existe(String palabra) { ... }
    public Optional<List<String>> getSignificados(String palabra) { ... }
    public String getIdioma() { ... }
}

public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios {
    public SuministradorDeDiccionariosDesdeFicheros(String carpeta) { ... }
    public boolean tienesDiccionarioDe(String idioma) { ... }
    public Optional<Diccionario> getDiccionarioDe(String idioma) { ... }
}
```

Backend: diccionario-desde-bbdd.jar

```java
package com.diccionarios.bbdd;
public class DiccionarioSobreBBDD implements Diccionario {
    public boolean existe(String palabra) { ... }
    public Optional<List<String>> getSignificados(String palabra) { ... }
    public String getIdioma() { ... }
}
 
public class SuministradorDeDiccionariosDesdeBBDD implements SuministradorDeDiccionarios {
    public SuministradorDeDiccionariosDesdeBBDD(Datos de la conexion a la BBDD) { ... }
    public boolean tienesDiccionarioDe(String idioma) { ... }
    public Optional<Diccionario> getDiccionarioDe(String idioma) { ... }
}
```

---
Frontal: aplicacion-diccionarios.jar

```java

import com.diccionarios.Diccionario;
import com.diccionarios.SuministradorDeDiccionarios;
//import com.diccionarios.ficheros.SuministradorDeDiccionariosDesdeFicheros; // ESTA LINEA ES LA MUERTE DEL PROYECTO!
 // Al introducir esa linea lo que acabamos es de cagarnos en el principio de inversión de dependencias (DIP)
public class BuscaPalabra {

  ///...

  private static void procesa(String palabra, String idioma, SuministradorDeDiccionarios suministrador) {
    //SuministradorDeDiccionarios suministrador = new SuministradorDeDiccionariosDesdeFicheros("c:/diccionarios"); // ESTA LINEA ES LA MUERTE DEL PROYECTO!

    if (!suministrador.tienesDiccionarioDe(idioma)) {
      System.out.println("No tengo diccionario de " + idioma);
      return;
    }

    Diccionario diccionario = suministrador.getDiccionarioDe(idioma).get();

    if (!diccionario.existe(palabra)) {
      System.out.println("La palabra no existe");
      return;
    }

    System.out.println("La palabra existe.");
    System.out.println("Significados:");
    for (String significado : diccionario.getSignificados(palabra).get()) {
      System.out.println("- " + significado);
    }
  }
}
```


    aplicacion-diccionarios.jar. ----> diccionarios-api.jar   <----diccionario-desde-ficheros.jar
          |                                                              ^
          |                                                              |
          +--------------------------------------------------------------+ <<< Esa dependencia es la muerte del proyecto
                                      

    aplicacion-diccionarios.jar. ----> diccionarios-api.jar   <----diccionario-desde-ficheros.jar


# TIO BOB (Robert C. Martin)

Principio de desarrollo de software.
En ciencias exactas, un principio es una verdad universal, que se cumple siempre, una ley, un mandamiento.

Pero...nosotros no estamos en una ciencia exacta. Nosotros no estamos en ciencias de la computación. Estamos en ingeniería del software.
En este caso, igual que en psicología, sociología, economía... un principio es un concepto (precepto) que me guía en la toma de decisiones.
Yo, como ser humano, tengo unos principios.

El tio bob, habló de 5 principios.. Los grandes 5 principios del desarrollo de software: SOLID
  S - Principio de Responsabilidad Única (Single Responsibility Principle)
  O - Principio de Abierto/Cerrado: Open/Closed Principle
  L - Principio de Sustitución de Liskov: Liskov Substitution Principle
  I - Principio de Segregación de Interfaces: Interface Segregation Principle
  D - Principio de Inversión de Dependencias: Dependency Inversion Principle
      Una clase no debe depender de implementaciones concretas de componentes de más bajo nivel. En su lugar ambos deben depender de abstracciones (interfaces).

Estos principios me garantizan que si los respeto, mi código será:
- Mantenible
- Reutilizable
- Testeable

Para respetar este principio, puedo hacer uso de ciertos PATRONES de desarrollo de software, muy probados y conocidos:
- Factoria (Factory)
- Inyección de dependencias (Dependency Injection)
  Esto es un patrón: Una forma de escribir CODIGO, que me ayuda a respetar el principio de inversión de dependencias (DIP)

  > Una clase nunca debe crear instancias de objetos que necesite. En su lugar, le deben ser suministradas esas instancias desde el exterior.

Este patrón esta guay... pero... al final, lo único que estamos haciendo es largar el problema a otro!
Eso es cierto.. intentaré que sea lo más arriba posible de mi proyecto.
Es más... aquí entran los Frameworks de inversión de control (IoC) como Spring.
Estos frameworks me facilitan el uso de patrón de inyección de dependencias (DI).

## Framework de inversión de control (IoC)

Quiero montar una etl: (programa que saca datos de un origen, los transforma y los mete en un destino):
- Ah... y que cuando comience a trabajar mande un correo.
- Ah... y que cuando tenga una persona procesada, la meta en una BBDD.
- Ah.. y si el dni no es válido, que lo mande a un fichero de errores. <--- REQUISITOS
- Ah... y que cuando lea una persona, al procesarla, le valide el dni.
- Ah.. y que cuando acabe mande otro correo.
- Lea datos de personas de un archivo EXCEL.

  ^^^ QUE TIPO DE LENGUAJE (PARADIGMA) estamos usando ahi? DECLARATIVO

// LINEA 1: Mandar un email
// LINEA 2: Abro el fichero excel
// LINEA 3: PARA CADA PERSONA EN EL EXCEL: FOR
  // LINEA 4:    Validar dni
  // LINEA 5:    Si dni no válido: MANDAR A FICHERO DE ERRORES : CONDICIONALES
  // LINEA 6:    Si dni válido: METER EN BBDD
// LINEA 7: Cerrar el fichero excel
// LINEA 8: Mandar otro email
  ^^^^
  QUE TIPO DE LENGUAJE (PARADIGMA) acabo de emplear al escribir el CODIGO? IMPERATIVO

Y ESE CODIGO DECLARATIVO es el que le damos a los FRAMEWORKS DE INVERSION DE CONTROL (IoC) como SPRING o CAMEL.
Cuando trabajamos con un framework de inversión de control, lo que hacemos es :
- DELEGAR en el framework el control del flujo de mi programa. NO LO CONTROLO YO.
- Es el framework el que controla la ejecución de mi código.
- HEMOS INVERTIDO EL CONTROL (IoC) del flujo del programa

En Spring, Angular, .net... el programa tiene una PUÑETERA LINEA DE CODIGO, que dice asi:
FrameworkQueEstoyUtilizando.ejecutaMiAplciación();

```java
// Spring

public class MiAplicacion {
  public static void main(String[] args) {
    SpringApplication.run(MiAplicacion.class, args); // Inversión de control. PONLE TU EL FLUJO A MI APLICACION!
    // FELIPE, que es tu responsabilidad!!!!
  }
}
```
El framework es quien creará una instancia de mi clase MiAplicacion, y cuando llame al método: 
  private static void procesa(String palabra, String idioma, SuministradorDeDiccionarios suministrador)

Se dará cuenta de que necesita un objeto de tipo SuministradorDeDiccionarios.
Y buscará en mi código si hay alguna clase que implemente esa interfaz.
Y si la encuentra, y la encontrará, el framework creará una instancia de esa clase, y se la pasará a mi método.

Y si el día de mañana, cambio esa implementación concreta de SuministradorDeDiccionarios,
por otra implementación concreta de SuministradorDeDiccionarios, 
al arrancar mi programa, el framework encontrará esa otra implementación concreta,
creará una instancia de esa otra implementación concreta, y se la pasará a mi método.

Y MI FUNCION NO TENGO QUE CAMBIARLA PARA NADA.

Claro que al final hay que hacer:
  
  > new SuministradorDeDiccionariosDesdeFicheros("c:/diccionarios");

Pero esa linea de código no la escribo yo... la escribe el framework.
