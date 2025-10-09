package com.curso.camel.casisistema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.curso.camel.model.PersonaIn;
import com.curso.camel.route.Ruta1BBDD2Kafka;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.camel.component.mock.MockEndpoint;

import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;

import com.curso.camel.model.PersonaOut;

import static org.mockito.Mockito.when;

import com.curso.camel.Aplicacion;

import static org.junit.jupiter.api.Assertions.*;

@CamelSpringBootTest
@SpringBootTest(classes = { Aplicacion.class})
// En este caso, vamos a arrancar la app de verdad de la buena.
// Y vamos a coger la ruta que ahi esta definida y vamos a probarla
// ...
// Lo que pasa es que en ese fichero tenemos un from(...) que me vale apra pruebas? NO
// Y tenemos un to(...) que me vale para pruebas? NO
// Pues... decisión: LES DAMOS EL CAMBIAZO!
@UseAdviceWith
// Esa anotación me permite tocar las rutas que ya existen en la aplicación antes de que se arranquen.
// Por ejemplo, cambiar el from(...) o el to(...) por otros endpoints que me sirvan para pruebas.
class PruebaRutaCompletaTest {


    // Constantes para datos de prueba
    private static final String ID = "1";
    private static final String DNI = "12345678A";
    private static final String NOMBRE = "Juan Pérez";
    private static final String CALLE = "Calle Mayor 123";
    private static final String CIUDAD = "Madrid";
    private static final String CODIGO_POSTAL = "28001";
    private static final String PAIS = "España";
    private static final String TELEFONO = "600123456";
    private static final String EMAIL = "juan@example.com";
    private static final int EDAD = 35;


    private final ProducerTemplate producerTemplate;

    // Este es el contexto de Camel que arranca con la app.. Dentro de ese contexto es donde están las rutas.
    // Al pedirlo en el constructor, Spring me lo inyecta.
    private CamelContext camelContext;

    @EndpointInject("mock:result") 
    private MockEndpoint mockResult;

    @Mock private PersonaIn personaIn;

    public PruebaRutaCompletaTest(@Autowired ProducerTemplate producerTemplate, @Autowired CamelContext camelContext) {
        this.producerTemplate = producerTemplate;
        this.camelContext = camelContext;
    }

    @BeforeEach
    void setUp() throws Exception {
        mockResult.reset();
        // Vamos a cambiar el from y el to de la ruta RUTA_ID
        AdviceWith.adviceWith(camelContext, Ruta1BBDD2Kafka.RUTA_ID, 
            ruta -> {
            // Cambiar el from por direct::start
            ruta.replaceFromWith("direct::start");
            // Cambiar el to por mock:result
            // Cambiar el ultimo to por mock:result
            // Eliminar el ultimo elemento: to
            ruta.weaveByToUri("kafka:*").replace().to("mock:result");
        });
        camelContext.start();
    }

    @Test
    void mandarMensajeConPersonaConDNIValido() throws Exception {
        when(personaIn.getId()).thenReturn(ID);
        when(personaIn.getDNI()).thenReturn(DNI);
        when(personaIn.getNombre()).thenReturn(NOMBRE);
        when(personaIn.getDireccion()).thenReturn(CALLE);
        when(personaIn.getPoblacion()).thenReturn(CIUDAD);
        when(personaIn.getCp()).thenReturn(CODIGO_POSTAL);
        when(personaIn.getPais()).thenReturn(PAIS);
        when(personaIn.getTelefono()).thenReturn(TELEFONO);
        when(personaIn.getEmail()).thenReturn(EMAIL);
 
 
        mockResult.expectedMessageCount(1);

        producerTemplate.sendBody("direct::start", personaIn );
        
        mockResult.assertIsSatisfied(); 
        // No me vale solo con comprobar que se ha recibido algo... En los filtros si.
        // Que me llega una PersonaOut...
        // Y además que llega con los datos correctos.
        // Lo que hay dentro no es un PersonaOut... es un XML
        // Pero el marshal de JacksonXMLDataFormat sabe convertir XML a objeto y viceversa.

        var xml = mockResult.getExchanges().get(0).getIn().getBody(String.class);

        // Convertir aquello a un objeto PersonaOut
        var transformador = new org.apache.camel.component.jacksonxml.JacksonXMLDataFormat();
        transformador.setUnmarshalType(PersonaOut.class);
        transformador.start();
        PersonaOut personaOut = (PersonaOut) transformador.unmarshal(mockResult.getExchanges().get(0), xml);

        // Comprobar que los datos son correctos

        assertNotNull(personaOut);
        assertEquals(ID, personaOut.getId());
        assertEquals(DNI, personaOut.getDNI());
        assertEquals(NOMBRE, personaOut.getNombre());
        assertEquals(EDAD, personaOut.getEdad());

        assertNotNull(personaOut.getDireccion());
        assertEquals(CALLE, personaOut.getDireccion().getCalle());
        assertEquals(CIUDAD, personaOut.getDireccion().getCiudad());
        assertEquals(CODIGO_POSTAL, personaOut.getDireccion().getCodigoPostal());
        assertEquals(PAIS, personaOut.getDireccion().getPais());

        assertNotNull(personaOut.getDatosContacto());
        assertNotNull(personaOut.getDatosContacto().getTelefonos());
        assertEquals(1, personaOut.getDatosContacto().getTelefonos().size());
        assertEquals(TELEFONO, personaOut.getDatosContacto().getTelefonos().get(0));
        assertNotNull(personaOut.getDatosContacto().getEmails());
        assertEquals(1, personaOut.getDatosContacto().getEmails().size());
        assertEquals(EMAIL, personaOut.getDatosContacto().getEmails().get(0));  
    }
}
