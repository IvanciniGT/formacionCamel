package com.curso.camel.processor.email;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class EmailUtilsTest {
    
    @ParameterizedTest
    @ValueSource(strings = {
        "usuario@ejemplo.com", 
        "nombre.apellido@empresa.es", 
        "test123@dominio.org",
        "contacto+tag@sitio.net",
        "admin@sub.dominio.com",
        "info_2024@ejemplo.co.uk"
    })
    void testEsEmailValido(String email) {
        assertTrue(EmailUtils.esEmailValido(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalido", 
        "@ejemplo.com", 
        "usuario@",
        "usuario @ejemplo.com",
        "usuario@ejemplo",
        "usuario..doble@ejemplo.com",
        "usuario@.ejemplo.com",
        "usuario@ejemplo..com",
        "",
        " "
    })
    void testEsEmailNoValido(String email) {
        assertFalse(EmailUtils.esEmailValido(email));
    }
}
