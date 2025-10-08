package com.curso.camel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Le indica a Spring que busque componentes y configuraciones en este paquete y en sus subpaquetes
public class Aplicacion {

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args); // Inversión de Control
    }
}