package com.curso.camel.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.curso.camel.model.PersonaIn;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.camel.component.mock.MockEndpoint;

import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import static org.mockito.Mockito.when;

// Esa anotación nos ayda a indicarle a Camel que estamos en modo pruebas... y nos permite 
// Que camel le mande información a Spring y a JUNIT
@CamelSpringBootTest
@SpringBootTest(classes = { AplicacionDePruebas.class})// Ejecutar una aplicación Spring Boot para pruebas de integración en paralelo con las pruebas.
@ActiveProfiles("test-integration") // Activar el perfil para cargar RutaParaPruebas
// El equivalente a esto en la app real:
/*    
    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args); // Inversión de Control
    }
 */
// Donde busca esta app los componentes? En el paquete actual y en sus subpaquetes
// Esa etiqueta también habilita a JUNIT a que le pida a Spring los componentes que necesite (inyección de dependencias)
class DNIFilterAndProcessorIntegrationTest {

    // Autowired inyecta automáticamente desde Spring una instancia de un ProducerTemplate
    // ProducerTemplate es un componente de Apache Camel que permite enviar mensajes a rutas Camel de manera programática.
    // En este caso, se utiliza para enviar mensajes a la ruta que se está probando.
    // ProducerTemplate producerTemplate;
    private final ProducerTemplate producerTemplate;

    // MockEndpoint es un componente de Apache Camel que se utiliza en pruebas para simular un endpoint real.
    @EndpointInject("mock:result") // Con esta linea estamos pidiendo a Camel que nos inyecte en esta variable el componente mock:result
    private MockEndpoint mockResult;

    @Mock private PersonaIn personaIn; // Me da una instancia de una implementación Dummy de PersonaIn

    // Este Autowired SI ES GUAY!... No usa reflection.
    // Solo es una pista a JUNIT para que ese parametro se lo pida a Spring
    public DNIFilterAndProcessorIntegrationTest(@Autowired ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @BeforeEach
    void setUp() {
        // Resetear el MockEndpoint antes de cada test para que no acumule mensajes
        mockResult.reset();
    }

    @Test
    void mandarMensajeSinPersona() throws Exception {
        // En nuestro caso, estábamos sacando en la ruta de pruebas el mensaje final al log(to)
        // Eso no lo queremos, ya que la prueba sebe acabar diciendo si ha ido bien o mal
        // No queremos nosotros estar viendo logs
        // Por lo tanto, la prueba debe acabar con una aserción

        // Que podemos hacer aqui?
        // Camel me da un componente de salida to(), que nos sirve para estas cosas... mock. 
        // Ese componente podemos configurarlo para que en lugar de sacar el mensaje a un log.
        // Y A ese componente le podemos preguntar si ha recibido o no mensajes

        // Para configurar ese componente, una vez lo hemos añadido a la ruta
        // Necesitamos tener una instancia de el.
        // Lo definimos en una variable arriba
        // Una vez tengo acceso a ese componente, puedo configurarlo
        // Le puedo decir que espero que reciba ningún mensaje en este caso, ya que el DNI es null

        mockResult.expectedMessageCount(0); // Espero que no reciba ningún mensaje

        // Querremos mandar un algo(mensaje) A la ruta Camel que hemos definido en RutaParaPruebas
        // La ruta empieza en "direct::start"
        // El mensaje que mandamos es una instancia de PersonaIn
        // El mensaje lo mandamos con el método sendBody del ProducerTemplate

        producerTemplate.sendBody("direct::start", null );

        // Una vez mandado el mensaje, podemos comprobar si el mock ha recibido o no mensajes
        mockResult.assertIsSatisfied(); // Comprueba si se han cumplido las expectativas del mock
    }

    @Test
    void mandarMensajeConPersonaSinDNI() throws Exception {
        mockResult.expectedMessageCount(0); // Espero que no reciba ningún mensaje
        producerTemplate.sendBody("direct::start", personaIn );
        mockResult.assertIsSatisfied(); // Comprueba si se han cumplido las expectativas del mock
    }

    @Test
    void mandarMensajeConPersonaConDNIInvalido() throws Exception {
        when(personaIn.getDNI()).thenReturn("Menchu");
        mockResult.expectedMessageCount(0); // Espero que no reciba ningún mensaje
        producerTemplate.sendBody("direct::start", personaIn );
        mockResult.assertIsSatisfied(); // Comprueba si se han cumplido las expectativas del mock
    }

    @Test
    void mandarMensajeConPersonaConDNIValido() throws Exception {
        when(personaIn.getDNI()).thenReturn("23000T");
        mockResult.expectedMessageCount(1); // Espero que no reciba ningún mensaje
        producerTemplate.sendBody("direct::start", personaIn );
        mockResult.assertIsSatisfied(); // Comprueba si se han cumplido las expectativas del mock
    }
}
