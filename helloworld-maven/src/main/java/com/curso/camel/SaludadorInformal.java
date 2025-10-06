package com.curso.camel;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;
@Component
// Esto ahora mismo solo genera una instancia del saludador. 
// Podemos hacer que se genera una instancia cada vez que se solicite un saludador?
//@Scope("prototype")
// El scope que viene por defecto es singleton
@Scope("singleton")
// @Primary // Indica a Spring que esta es la implementación preferida cuando se solicite un Saludador
// ^^^ Así no lo usamos mucho... Esto se usa cuando hacemos pruebas... y queremos montar un FAKE
// Es decir mi código ya tiene un Saludador... pero... joder... tarda 20 segundos en saludar...
// Y requiere de una conexión a una base de datos... y yo solo quiero hacer pruebas...
// Monto una implementación de mentirijilla del Saludador... que siempre saca: "HOLA" ... y va rápido... y no necsita BBDD... 
// Y para pruebas, me sirve... y le digo a Spring que cuando estemos en modo pruebas, use este saludador... que es el preferido

// Otra opción que resuelve otro problema es que tenga varias implementaciones de saludador que sean consumidas por diferentes clases
// Impresor1 <- Saludador
// Impresor2 <- Otro Saludador diferente
// No me quiero atar a una implementación concreta, es decir, no haría:
// Impresor 1 <- SaludadorFormal
// Impresor 2 <- SaludadorInformal
// Que el día de mañana si cambio el saludador formal, tengo que cambiar el código del Impresor1

// Lo planteo de otra forma... CUALIFICO los beans y la inyección de dependencias
@Qualifier("tipo1")

public class SaludadorInformal implements Saludador {

    //private final Impresor impresor;
    private final String plantillaDeSaludo; // Esta variable la rellenamos pidiendo a Spring que lea el valor de su fichero de propiedades

    public SaludadorInformal( @Value("${saludador.informal.plantilla}") String plantillaDeSaludo ){ // @Lazy Impresor impresor ) {
        System.out.println("Creando una instancia de SaludadorInformal con impresor: " );
        this.plantillaDeSaludo = plantillaDeSaludo;
        //this.impresor = impresor;
    }

    @Override
    public String generarSaludo(String nombrePersona) {
        return String.format(plantillaDeSaludo, nombrePersona);
    }

}