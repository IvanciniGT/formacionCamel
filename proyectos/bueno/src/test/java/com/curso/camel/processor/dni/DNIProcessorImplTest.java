package com.curso.camel.processor.dni;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;

import com.curso.camel.model.PersonaIn;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

// Tenemos una clase de pruebas JUNIT, que es el gran framework de pruebas en Java
// Lo ejecutamos mediante 
//     $ mvn test
// Quién ejecuta las pruebas? Maven... pero... maven no sabe hacer la "o" con un canuto
// Maven delega todos los trabajos a PLUGINS. En concreto las pruebas se las delega al Surefire Plugin
// El Surefire Plugin es el que sabe ejecutar las pruebas JUnit
// El plugin surefire a su vez, le pide a JUnit que ejecute las pruebas
// Aquí hay una clase.. JAVA: DNIProcessorTest... Quién crea la instancia de esta clase? JUNIT ... "new DNIProcessorTest()"

// Con esta anotación, le decimos a JUnit que use active el motor de Mockito para gestionar los mocks
// De forma que si alguien le pone @Mock a un atributo, Mockito se encargue de crear la instancia "mockeada"
@ExtendWith(MockitoExtension.class)
class DNIProcessorTest {
    
    private DNIProcessor dniProcessor = new DNIProcessorImpl();
    private Exchange exchange;
    /*
     Esto nos hace una imitación de un objeto de tipo PersonaIn Dummy
     Es decir, es una instancia de PersonaIn cuyos métodos devuelven lo más básico posible
        Si una función devolverá un Objeto, devolverá null
        Si una función devolverá un numérico, devolverá 0
        Si una función devolverá un booleano, devolverá false
     Nos hará falta para la prueba mockear la función getDni() para que devuelva el DNI que queramos
     */
    @Mock private PersonaIn personaIn;
    /* Mokito lo que escribe es esta clase:
     public class PersonaIn {
            public String getId(){ return null; }
            public void setId(String id){}
            public String getDNI(){ return null; }
            public void setDNI(String dni){}
            public String getNombre(){ return null; }
            public void setNombre(String nombre){}
            public LocalDate getFechaDeNacimiento(){ return null; }
            public void setFechaDeNacimiento(LocalDate fechaDeNacimiento){}
            public String getDireccion(){ return null; }
            public void setDireccion(String direccion){}
            public String getPoblacion(){ return null; }
            public void setPoblacion(String poblacion){}
            public String getCp(){ return null; }
            public void setCp(String cp){}
            public String getPais(){ return null; }
            public void setPais(String pais){}
            public String getTelefono(){ return null; }
            public void setTelefono(String telefono){}
            public String getEmail(){ return null; }
            public void setEmail(String email){}
        }
            De esa clase, genera una instancia y la pone en el atributo personaIn
     */
    // Esta anotación, le dice a JUNIT que ejecute este método antes de cada prueba
    @BeforeEach
    void setUp() {
        // Antes de cada prueba, generar un nuevo Exchange y le pongo en el cuerpo del mensaje el objeto personaIn
        var camelContext = new DefaultCamelContext();
        exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(personaIn);
    }

    @ParameterizedTest
    @ValueSource(strings = {"23T", "23.000 T", "23000T", "23000T", "23.000 T" , "23.000.000T", "23000000T", "23000000T", "23.000.000-t"})
    void testEsDNIValido(String dni) throws Exception {
        // Es el contexto en el que lo pruebo                                       GIVEN
        // Mockear el método getDni() para que devuelva el DNI que queremos
        when(personaIn.getDNI()).thenReturn(dni);
        // Esto lo que pruebo                                                       WHEN  
        dniProcessor.process(exchange);                             // Cuando proceso el exchange con el procesador de DNI
        // Condiciones que espero que se cumplan para que la prueba pase            THEN
        // ¿SABÉIS QUE ESTAMOS HACIENDO AQUÍ? ¿EN ESTE PUNTO? ESPECIFICAR EL COMPORTAMIENTO QUE TIENE QUE TENER EL PROCESADOR
        assertTrue(exchange.getProperties().containsKey(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME)); // Espero que en las propiedades del exchange exista la propiedad dniValido
        // Y que esa propiedad sea true
        assertEquals(Boolean.TRUE, exchange.getProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME));
    }

    @ParameterizedTest
    @ValueSource(strings = {"23X", "23.000 X", "23000X", "23000X ", "23.000 X" , "23.000.000X", "23000000X", " 23000000X ", "23.000.000-x",
                            "123456789", "A2345678Z", "1234.567Z", "12.34567Z", "123.4567Z", "123.456.789Z"})
    void testEsDNINoValido(String dni) throws Exception {
        // Mockear el método getDni() para que devuelva el DNI que queremos
        when(personaIn.getDNI()).thenReturn(dni);
        dniProcessor.process(exchange);
        assertTrue(exchange.getProperties().containsKey(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME));
        assertEquals(Boolean.FALSE, exchange.getProperty(DNIProcessor.DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME));
    }

}
