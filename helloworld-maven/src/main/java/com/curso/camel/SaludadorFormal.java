package com.curso.camel;

//import org.springframework.stereotype.Component;

//@Component // Spring, cuando alguien solicite un Saludador, le entregas una instancia de esta clase.
public class SaludadorFormal implements Saludador {

    public SaludadorFormal() {
        System.out.println("Creando una instancia de SaludadorFormal");
    }


    @Override
    public String generarSaludo(String nombrePersona) {
        return "Estimado/a " + nombrePersona + ", es un placer saludarle.";
    }

}

// Yo , oh creador del SaludadorFormal... DIGO QUE SOY EL SALUDADOR QUE DEBE USARSE EN LA APLICACION !