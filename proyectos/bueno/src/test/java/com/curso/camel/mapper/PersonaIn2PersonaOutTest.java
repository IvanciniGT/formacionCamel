package com.curso.camel.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.curso.camel.model.PersonaIn;
import com.curso.camel.model.PersonaOutImpl;
import com.curso.camel.model.PersonaOut;
import com.curso.camel.processor.edad.EdadProcessor;

@ExtendWith(MockitoExtension.class)
class PersonaIn2PersonaOutTest {
    
    // Constantes para datos de prueba
    private static final Long ID = 1L;
    private static final String DNI = "12345678A";
    private static final String NOMBRE = "Juan Pérez";
    private static final String CALLE = "Calle Mayor 123";
    private static final String CIUDAD = "Madrid";
    private static final String CODIGO_POSTAL = "28001";
    private static final String PAIS = "España";
    private static final String TELEFONO = "600123456";
    private static final String EMAIL = "juan@example.com";
    private static final int EDAD = 35;
    
    @Mock
    private PersonaIn personaIn;
    
    private PersonaIn2PersonaOutMapper mapper;
    private DefaultExchange exchange;
    
    @BeforeEach
    void setUp() {
        mapper = new PersonaIn2PersonaOutMapper();
        var camelContext = new DefaultCamelContext();
        exchange = new DefaultExchange(camelContext);
    }
    
    @Test
    void debeMapearCorrectamentePersonaInAPersonaOut() throws Exception {
        // Given - Configurar el mock de PersonaIn
        when(personaIn.getId()).thenReturn(ID);
        when(personaIn.getDNI()).thenReturn(DNI);
        when(personaIn.getNombre()).thenReturn(NOMBRE);
        when(personaIn.getDireccion()).thenReturn(CALLE);
        when(personaIn.getPoblacion()).thenReturn(CIUDAD);
        when(personaIn.getCp()).thenReturn(CODIGO_POSTAL);
        when(personaIn.getPais()).thenReturn(PAIS);
        when(personaIn.getTelefono()).thenReturn(TELEFONO);
        when(personaIn.getEmail()).thenReturn(EMAIL);
        
        // Establecer la edad en el Exchange (como lo haría el EdadProcessor)
        exchange.setProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME, EDAD);
        
        // Establecer el PersonaIn en el body del exchange
        exchange.getIn().setBody(personaIn);
        
        // When - Ejecutar el mapper
        mapper.process(exchange);
        
        // Then - Verificar que el PersonaOut se ha creado correctamente
        PersonaOut personaOut = exchange.getIn().getBody(PersonaOutImpl.class);
        
        assertNotNull(personaOut);
        assertEquals(ID, personaOut.getId());
        assertEquals(DNI, personaOut.getDNI());
        assertEquals(NOMBRE, personaOut.getNombre());
        assertEquals(EDAD, personaOut.getEdad());
        
        // Verificar Direccion
        assertNotNull(personaOut.getDireccion());
        assertEquals(CALLE, personaOut.getDireccion().getCalle());
        assertEquals(CIUDAD, personaOut.getDireccion().getCiudad());
        assertEquals(CODIGO_POSTAL, personaOut.getDireccion().getCodigoPostal());
        assertEquals(PAIS, personaOut.getDireccion().getPais());
        
        // Verificar DatosContacto
        assertNotNull(personaOut.getDatosContacto());
        assertNotNull(personaOut.getDatosContacto().getTelefonos());
        assertEquals(1, personaOut.getDatosContacto().getTelefonos().size());
        assertEquals(TELEFONO, personaOut.getDatosContacto().getTelefonos().get(0));
        
        assertNotNull(personaOut.getDatosContacto().getEmails());
        assertEquals(1, personaOut.getDatosContacto().getEmails().size());
        assertEquals(EMAIL, personaOut.getDatosContacto().getEmails().get(0));
    }
}
