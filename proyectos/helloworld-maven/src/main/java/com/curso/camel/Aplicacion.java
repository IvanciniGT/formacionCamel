package com.curso.camel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Le indica a Spring que busque componentes y configuraciones en este paquete y en sus subpaquetes
public class Aplicacion {

    public static void main(String[] args) {
        System.out.println("Arrancando la aplicación...");
        SpringApplication.run(Aplicacion.class, args); // Inversión de Control
        // Le pido a Spring que añada flujo a mi aplicación y la ejecute.
        // Y que flujo va a poner? El que se define el el código interno de Spring...
        // Básicamente el que os he contado antes... o una parte al menos os he contado.
        // 1. Arranca el contenedor de Spring
        // 2. Escanea las clases en busca de componentes (clases anotadas con @Component o @Configuration)
        // 3. Crea instancias de esas clases y las guarda en un contenedor
        // 4. Esas clases las va creando en orden... en base a las dependencias que tengan entre ellas.
        // 5. Cuando termina de crear todas las clases, sigue.....
    }
}