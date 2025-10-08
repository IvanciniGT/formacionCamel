package com.curso.camel.processor.dni;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class DNIUtilsTest {
    
    @ParameterizedTest
    @ValueSource(strings = {"23T", "23.000 T", "23000T", "23000T", "23.000 T" , "23.000.000T", "23000000T", "23000000T", "23.000.000-t"})
    void testEsDNIValido(String dni) {
        assertTrue(DNIUtils.esDNIValido(dni));
    }

    @ParameterizedTest
    @ValueSource(strings = {"23X", "23.000 X", "23000X", "23000X ", "23.000 X" , "23.000.000X", "23000000X", " 23000000X ", "23.000.000-x",
                            "123456789", "A2345678Z", "1234.567Z", "12.34567Z", "123.4567Z", "123.456.789Z"})
    void testEsDNINoValido(String dni) {
        assertFalse(DNIUtils.esDNIValido(dni));
    }
}
