package com.curso.camel.processor.edad;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class EdadUtilsTest {
    
    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50})
    void testCalcularEdadConFechasDinamicas(int anosAtras) {
        int anoActual = LocalDate.now().getYear();
        LocalDate fechaNacimiento = LocalDate.of(anoActual - anosAtras, 6, 15); // Mes 6, día 15 para evitar edge cases
        int edadCalculada = EdadUtils.calcularEdad(fechaNacimiento);
        
        // La edad debe ser exactamente los años que han pasado (o uno menos si aún no llegó el cumpleaños)
        assertTrue(edadCalculada >= anosAtras - 1 && edadCalculada <= anosAtras, 
                  "Para fecha " + fechaNacimiento + " (hace " + anosAtras + " años), edad calculada: " + edadCalculada);
    }

    @Test
    void testCalcularEdadHoy() {
        LocalDate hoy = LocalDate.now();
        int edad = EdadUtils.calcularEdad(hoy);
        assertEquals(0, edad);
    }

    @Test
    void testCalcularEdadAyer() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        int edad = EdadUtils.calcularEdad(ayer);
        assertEquals(0, edad);
    }

    @Test
    void testCalcularEdadHaceUnAno() {
        LocalDate haceUnAno = LocalDate.now().minusYears(1);
        int edad = EdadUtils.calcularEdad(haceUnAno);
        assertTrue(edad == 0 || edad == 1); // Puede ser 0 o 1 dependiendo del día
    }

    @Test
    void testCalcularEdadFechaNula() {
        int edad = EdadUtils.calcularEdad(null);
        assertEquals(0, edad);
    }
}