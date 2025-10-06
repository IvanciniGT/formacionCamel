package com.curso.camel;

// Pregunta...
// Yo, oh! Creador del Impresor... Me importa algo quién sea el saludador que se esté usando? NO
// Cual es mi trabajo... como creador (desarrollador) del Impresor? Cuales son los requisitos funcionales del Impresor?
// 1. Generar un saludo con un saludador
// 2. Imprimir ese saludo por pantalla

// Que saludador se usa? ME DA IGUAL... a MI ME DA IGUAL... eso será problema de otro... el que configure este tinglao.
// El dia de mañana pueden poner un saludador X ... y al siguiente otro saludador Y... pero a mi me da igual!

public class Impresor {

    private final Saludador saludador;

    public Impresor(Saludador saludador) { // Solicitando una inyección de dependencias!
                                           // Y Esto funcionará SI Y SOLO SI el constructor es invocado por SPRING
                                           // new Impresor(..); Solo si esto es invocado por SPRING, funcionará... y me será entregado 
                                           // un saludador... Cual? NO ES MI PROBLEMA! 
                                           // Aquí entra otro principio muy bonito : SoC: Separation of Concerns
        System.out.println("Creando una instancia de Impresor");
        this.saludador = saludador;
    }

    public void imprimir(String nombre) {
        System.out.println(saludador.generarSaludo(nombre));
    }

}