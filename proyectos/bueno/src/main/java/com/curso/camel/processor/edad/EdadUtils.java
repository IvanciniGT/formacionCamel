package com.curso.camel.processor.edad;

import java.time.LocalDate;
import java.time.Period;

public class EdadUtils {

    private EdadUtils() {
        // Constructor privado para evitar instanciación
    }
    
    public static int calcularEdad(LocalDate fechaDeNacimiento) {
        if (fechaDeNacimiento == null) {
            return 0;
        }
        LocalDate fechaActual = LocalDate.now();
        return Period.between(fechaDeNacimiento, fechaActual).getYears();
    }

}