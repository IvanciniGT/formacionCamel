
# CAMEL:

Framework java para integración de aplicaciones. Usa un lenguaje declarativo.
Usa patrones de integración conocidos y documentados.
Nos permite un desarrollo muy rápido.

Lo haremos funcionar usando Spring.

# Spring?

Otro Framework de JAVA para inversión de control:
Nosotros no vamos a poner el flujo de nuestro programa, sino que delegamos ese trabajo en el framework.
2 ventajas:
- Menos código que escribir
- Facilidad para usar un patrón de inyección de dependencias.

# Patrón de inyección de dependencias?

Una clase no debe crear instancias de objetos que necesita.
En su lugar, le deben ser suministrados.

Al hacer eso, garantizamos el cumplimiento del principio de inversión de dependencias.

# Principio de inversión de dependencias

Un componente de alto nivel no debe depender de implementaciones de componentes de bajo nivel. Ambos deben depender de abstracciones.

BASICAMENTE Que una clase no puede importar otras clases.. solo interfaces.

El problema es que con el patrón de inyección de dependencias, se posterga el problema... se le delega a otro la responsabilidad de crear las instancias necesarias.

Aquí es donde entra el framework de inversión de control. Al ser él quien va creando instancias de mis objetos, les entrega lo que necesiten... generando instancias adecuadas.


# Programación funcional.

Y decíamos que en camel usamos mucho la programación funcional.

De qué iba este concepto?
Cuando el lenguaje me permite que una variable apunte a una función y posteriormente invocar (ejecutar) esa función a través de la variable

# MAVEN

Herramienta de automatización de tareas:
- Compilar
- Empaquetar
- Probar
- Documentar
- Mandar un git
- Mandar a un sonarqube
- Y ... gestionar dependencias!

---

Spring... SpringBoot.

Spring es un framework para inversión de control. SpringBoot es librería que extiende ese framework.. que nos hace más fácil el proceso de configurar y usar Spring. Antiguamente Spring se usaba creando y configurando muchos archivos XML. SpringBoot nos permite hacer todo eso con anotaciones en las clases = GUAY! Pero... hay mucha magia.

---

Hemos dicho que Spring(Springboot) nos van a ayudar a aplicar patrones de inyección de dependencias. COMO?

# Cómo le pido a Spring una Dependencia?

```java

public interface MiInterfaz {
    void hacerAlgo();
}

public class MiClaseQueImplementa implements MiInterfaz {
    @Override
    public void hacerAlgo() {
        System.out.println("Haciendo algo");
    }
}

import MiInterfaz;
//import MiClaseQueImplementa; // Y ESTA ES LA PISOTEADA QUE HACEMOS DEL PPO DE INVERSION DE DEPENDENCIAS
public class MiClase {

    private void miFuncion() {
        // Quiero una instancia de MiDependencia
        MiInterfaz objetoDelQueDependo // = new MiClaseQueImplementa(); // CAGADA !
        objetoDelQueDependo.hacerAlgo();
    }

}
```

> OPCION 1 Mi clase puede pedir ese objeto que necesita en en cualquier función que lo necesite.

```java
public class MiClase {

    private void miFuncion(MiInterfaz objetoDelQueDependo) {
        objetoDelQueDependo.hacerAlgo();
    }
}
```

Y ese código funciona en Spring perfecto... Siempre y cuando sea Spring quién llame a miFuncion.
En ese caso, Spring mira qué argumentos necesita miFuncion para poder llamarla.
Y al ver que necesita una instancia de un objeto que implemente MiInterfaz, Spring se encarga de crear una instancia de una clase que implemente MiInterfaz y pasársela a miFuncion.

> OPCION 2 Mi clase puede pedir ese objeto que necesita en su constructor y guardarlo como una variable de instancia privada.

```java
public class MiClase {
    private final MiInterfaz objetoDelQueDependo;

    public MiClase(MiInterfaz objetoDelQueDependo) {
        this.objetoDelQueDependo = objetoDelQueDependo;
    }

    private void miFuncion() {
        objetoDelQueDependo.hacerAlgo();
    }
}
```

```java
// Esto es lo que Spring escribirá por mi
MiInterfaz miInterfaz = new MiClaseQueImplementa();
MiClase miClase = new MiClase(miInterfaz);
```

Este código en Spring funciona perfecto... siempre y cuando sea Spring quien cree la instancia de MiClase.
Si Spring crea la instancia de MiClase, Spring es quién llamará al constructor .
Y para llamarlo lo primero que hace es mirar que necesita pasarle.
Spring será el responsable de crear una instancia de una clase que implemente MiInterfaz y pasársela al constructor.
Es decir... el "new MiClaseQueImplementa()" lo hará Spring. Es más... es código ni lo vamos a ver! Y eso es lo que nos puede despistar.
Es solo una variante de la opción 1.

@Autowired = ESTO ESTA PROHIBIDO YA HOY EN DIA... solo en casos muy puntuales lo usamos. La forma preferida hoy en día en Spring es mediante el CONSTRUCTOR (OPCIÓN 2)

```java
import org.springframework.beans.factory.annotation.Autowired;
public class MiClase {
    @Autowired // Esto le decía a Spring, que cuando crease él (sigue siendo un requisito) una instancia de MiClase, le inyectase una instancia de MiInterfaz en esta variable.
    private MiInterfaz objetoDelQueDependo;

    private void miFuncion() {
        objetoDelQueDependo.hacerAlgo();
    }
}
```

Esto no se usa. Por qué?

```java
// Esto es lo que Spring escribirá por mi
MiInterfaz miInterfaz = new MiClaseQueImplementa();
MiClase miClase = new MiClase();
miClase.objetoDelQueDependo = miInterfaz; // Esto no funcionaría.. La variable es privada... y Spring no tiene derecho a establecer esa variable.
// @Autowired usa un mecanismo hoy en día proscrito! Ese mecanismo es el de la reflexión. Básicamente es una "habilidad" que tiene Java para tocar datos de objetos directamente en memoria RAM, saltándose todas las reglas de visibilidad y encapsulamiento = PELIGROSISISISMO.
// De hecho en las últimas versiones de Java, se está limitando mucho el uso de la reflexión. Se considera una muy mala práctica...
// Además tiene otra cosita... ES MUY LENTO!
```



---

# Cómo le digo a Spring lo que debe entregar cuando alguien pide una dependencia?

```java

public interface MiInterfaz {
    void hacerAlgo();
}

public class MiClaseQueImplementa implements MiInterfaz {
    @Override
    public void hacerAlgo() {
        System.out.println("Haciendo algo");
    }
}

import MiInterfaz;
public class MiClase {
    private final MiInterfaz objetoDelQueDependo;

    public MiClase(MiInterfaz objetoDelQueDependo) {
        this.objetoDelQueDependo = objetoDelQueDependo;
    }

    private void miFuncion() {
        objetoDelQueDependo.hacerAlgo();
    }
}
```
Cómo sabe Spring qué debe entregar? 2 opciones:

> OPCION 1: Anotando la clase de la que quiero que se me entregue la instancia con @Component

```java

import org.springframework.stereotype.Component;

@Component
public class MiClaseQueImplementa implements MiInterfaz {
    @Override
    public void hacerAlgo() {
        System.out.println("Haciendo algo");
    }
}
```
RESUELTO!
@ Component, encima de una clase que implementa una interfaz, le dice a Spring que cuando alguien pida una instancia de esa interfaz, debe crear una instancia de esta clase y entregarla.

Si tenemos varias clases que implementan una interfaz.... :
2 opciones:
- Si alguien está pidiendo solo una, Spring explota y no arranca siquiera.
- Si alguien pide una lista de instancias de esa interfaz, Spring crea una instancia de cada de ellas, las mete en una lista y se la entrega.
- Si alguien pide una instancia concreta, usamos @Qualifier (no lo veremos hoy)

Si la opción 1 me sirve... DEJO DE LEER ESTE DOCUMENTO... 

Cuando esa opción no serviría? BASICAMENTE CUANDO EL CODIGO DE ESA CLASE NO ES MIO... NO PUEDO TOCARLO.

Imaginad que esa clase la provee una librería de terceros... no puedo tocarla para ponerle un @Component.

Entonces entra la opción 2:

> OPCION 2: Crear una clase de configuración de Spring con métodos anotados con @Bean

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Esto le dice a Spring que en esta clase estamos definiendo BEANS
               // Un BEAN es la instancia que Spring debe crear y entregar cuando alguien pide una dependencia.
public class MiConfiguracion {

    @Bean // Con esta anotación le digo a Spring que si alguien pide una instancia de MiInterfaz, 
          // debe llamar a este método y entregarle lo que devuelva esta función
    public MiInterfaz federico() { // El nombre de la frunción NO IMPORTA
        return new MiClaseQueImplementa();
    }

}
```

AQUI HAY UNA COSITA MAS... tanto en la opción 1 como en la 2.... Para que esto funcione, Spring debe haber leido previamente (durante el arranque de la app) estas clases. (o bien las que están marcadas con @Component o bien las que están marcadas con @Configuration)
Si no le fuerzo a que lea esas clases, esto no va a funcionar!

Realmente funciona un poco diferente a lo que os he contado.

REALIDAD: 
En el arranque Spring busca todas las clases que estén anotadas con @Component o @Configuration de los paquetes que yo le indique. Las que encuentra, el directamente crea una instancia de cada una de ellas y las guarda en una cache (contexto de Spring). Cuando luego alguien pide un objeto de uno de esos tipos, Spring lo devuelve de la caché. Ese es el funcionamiento por defecto que tiene Spring.... que puede cambiarse.. y alguna vez lo hacemos (<5%).

Eso que acabo de poner ahí implica que si 700 sitios diferentes de mi código piden un MiInterfaz, a todos ellos se les manda la misma instancia. Dicho de otra forma, Spring se asegura que solo habrá una única instancia de cada clase que haya sido anotada con @Component o @Configuration.

Antiguamente nosotros en java usábamos un patrón de diseño que nos ofrecia este mismo comportamiento: SINGLETON

```java

public class MiSingleton  {
    private static final volatile MiSingleton instanciaUnica = null;

    private MiSingleton() {
        // Constructor privado para que nadie pueda crear instancias desde fuera
    }

    public static MiSingleton getInstancia() {
        if (instanciaUnica == null) { // Evitar que el sincronized se ejecute si no es necesario. Es una operación muy costosa.
            synchronized (MiSingleton.class) { // Evitar o que se denomina una condición de carrera
                if (instanciaUnica == null) {
                    instanciaUnica = new MiSingleton();
                }
            }
        }
        return instanciaUnica;
    }
}
```


Todo ese código ahora me lo ahorro gracias a Spring. En ese código yo forzaba el que de esa clase solo hubiese una única instancia. 
Hoy en día ese trabajo de alguna forma lo hace Spring por mi. El crea la instancia, la guarda en una cache y cuando alguien la pide, se la entrega.

* NOTA: Sobre la palabra volatile. Las CPUS tiene una MEMORIA CACHE!
La palabra volatile le indica a JAVA que esa variable puede ser manipulada desde 2 hilos diferentes.... y que no debe usar la cache de la CPU para leerla o escribirla... sino que debe ir siempre a la RAM.


---

Añadir las dependencias de Spring a mi proyecto.

Spring tiene cientos de módulos diferentes.
Cómo se cuales debo añadir a mi proyecto?

Springboot me echa una mano. Mediante el concepto de Starters.

Son conjuntos de dependencias que me permiten hacer algo concreto.
De hecho Springboot define más de 50 starters diferentes.

Cuando trabajo con proyectos Spring en JAVA con maven, me interesa dar de alta otra cosa en el fichero pom.xml
EL PLUGIN DE SPRING. Este plugin me permite arrancar directamente desde maven aplicaciones Spring... En realidad lo que me permite es pedirle a maven que sea él quien arranque la aplicación de spring... configurando el CLASSPATH con las dependencias necesarias.


---

Para ejecutar un código en paralelo no necesito 2 instancias de un objeto...
Necesito 2 hilos recorriendo el código de ese objeto.

El código no se ejecuta solo. El código lo ejecuta un hilo de ejecución THREAD.
Esos THREADS son gestionados por el sistema operativo... se abren dentro de un proceso (fork)

Esos hilos son los que recorren el código de mis objetos.
Y puedes tener el mismo código del mismo objeto siendo recorrido por varios hilos a la vez.

---

Parametrización de nuestro código.

En muchos sitios del código habrá datos que queramos parametrizar. 
Pueden ser datos que varien entre entornos (dev, pre, prod)...
Pueden ser datos que varien entre distintas apps que queramos conectar con camel...

Spring ... las apps spring tienen su propio fichero de propiedades.
Ese fichero lo podemos escribir con 2 sintaxis diferentes:
- .properties
- .yaml
Por defecto Spring busca un fichero llamado application.properties o application.yaml en la raíz del classpath.

Para nosotros es sencillo. Nos basta con colocar un fichero con ese nombre dentro de src/main/resources.
YA MAVEN se encarga de copiarlo al classpath cuando compila... y de empaquetarlo dentro del JAR cuando empaqueta la app para producción.

En ese fichero puedo poner las propiedades que me de la gana.

Y después, cuando tenga una clase que necesite una de esas propiedades, se la pido a Spring.

Eso lo haremos con un anotación llamada @Value

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class MiClase {
    private final String urlServidor;
    private final int puertoServidor;

    public MiClase(@Value("${miapp.urlservidor}") String urlServidor,
                   @Value("${miapp.puertoservidor}") int puertoServidor) {
        this.urlServidor = urlServidor;
        this.puertoServidor = puertoServidor;
    }
}
```