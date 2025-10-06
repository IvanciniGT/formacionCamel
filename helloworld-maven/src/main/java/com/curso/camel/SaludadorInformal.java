package com.curso.camel;

import org.springframework.stereotype.Component;

@Component
public class SaludadorInformal implements Saludador {

    public SaludadorInformal() {
        System.out.println("Creando una instancia de SaludadorInformal");
    }

    @Override
    public String generarSaludo(String nombrePersona) {
        return "Hola " + nombrePersona + ", ¿qué tal?";
    }

}