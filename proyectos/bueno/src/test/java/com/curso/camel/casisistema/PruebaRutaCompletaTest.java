package com.curso.camel.casisistema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.curso.camel.model.PersonaIn;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.camel.component.mock.MockEndpoint;

import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;

import static org.mockito.Mockito.when;

import com.curso.camel.Aplicacion;

import static org.junit.jupiter.api.Assertions.*;

@CamelSpringBootTest
@SpringBootTest(classes = { Aplicacion.class })
// En este caso, vamos a arrancar la app de verdad de la buena.
// Y vamos a coger la ruta que ahi esta definida y vamos a probarla
// ...
// Lo que pasa es que en ese fichero tenemos un from(...) que me vale apra pruebas? NO
// Y tenemos un to(...) que me vale para pruebas? NO
// Pues... decisión: LES DAMOS EL CAMBIAZO!
@UseAdviceWith
// Esa anotación me permite tocar las rutas que ya existen en la aplicación antes de que se arranquen.
// Por ejemplo, cambiar el from(...) o el to(...) por otros endpoints que me sirvan para pruebas.
@org.springframework.boot.test.context.TestConfiguration
class PruebaRutaCompletaTest {


    // Constantes para datos de prueba
    private static final String ID = "1";
    private static final String DNI = "12345678Z"; // DNI válido según el algoritmo
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
        
        // Usar AdviceWith para cambiar los endpoints reales (BBDD/Kafka) por endpoints de prueba
        AdviceWith.adviceWith(camelContext, "ruta1-bbdd2kafka", a -> {
            // Reemplazar el origen (base de datos) por direct:start
            a.replaceFromWith("direct::start");
            
            // Reemplazar el destino (Kafka) por mock:result
            a.weaveByToUri("*").replace().to("mock:result");
        });
        
        camelContext.start();
    }

    @Test
    void mandarMensajeConPersonaConDNIValido() throws Exception {
        when(personaIn.getId()).thenReturn(ID);
        when(personaIn.getDNI()).thenReturn(DNI);
        when(personaIn.getNombre()).thenReturn(NOMBRE);
        when(personaIn.getFechaDeNacimiento()).thenReturn(java.time.LocalDate.of(1989, 11, 1)); // Para edad 35
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

        var xml = mockResult.getExchanges().get(0).getIn().getBody(String.class);

        // 1. VALIDAR EL XML CONTRA EL ESQUEMA XSD
        assertNotNull(xml, "El XML no debe ser nulo");
        
        // Cargar el esquema XSD
        var schemaFactory = javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        var schemaFile = new java.io.File("src/main/resources/persona-out.xsd");
        var schema = schemaFactory.newSchema(schemaFile);
        var validator = schema.newValidator();
        
        // Validar el XML contra el esquema
        validator.validate(new javax.xml.transform.stream.StreamSource(new java.io.StringReader(xml)));
        
        // 2. PARSEAR EL XML Y EXTRAER LOS DATOS
        var documentBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        var root = document.getDocumentElement();
        
        // Verificar el atributo id
        assertEquals(ID, root.getAttribute("id"), "El ID debe ser correcto");
        
        // Verificar elementos de primer nivel
        assertEquals(DNI, getElementText(root, "DNI"), "El DNI debe ser correcto");
        assertEquals(NOMBRE, getElementText(root, "Nombre"), "El nombre debe ser correcto");
        assertEquals(String.valueOf(EDAD), getElementText(root, "Edad"), "La edad debe ser correcta");
        
        // Verificar dirección
        var direccion = (org.w3c.dom.Element) root.getElementsByTagName("Direccion").item(0);
        assertNotNull(direccion, "Debe existir el elemento Direccion");
        assertEquals(CALLE, getElementText(direccion, "Calle"), "La calle debe ser correcta");
        assertEquals(CIUDAD, getElementText(direccion, "Ciudad"), "La ciudad debe ser correcta");
        assertEquals(CODIGO_POSTAL, getElementText(direccion, "CodigoPostal"), "El código postal debe ser correcto");
        assertEquals(PAIS, getElementText(direccion, "Pais"), "El país debe ser correcto");
        
        // Verificar datos de contacto
        var datosContacto = (org.w3c.dom.Element) root.getElementsByTagName("DatosContacto").item(0);
        assertNotNull(datosContacto, "Debe existir el elemento DatosContacto");
        
        var telefonos = datosContacto.getElementsByTagName("Telefono");
        assertEquals(1, telefonos.getLength(), "Debe haber un teléfono");
        assertEquals(TELEFONO, telefonos.item(0).getTextContent(), "El teléfono debe ser correcto");
        
        var emails = datosContacto.getElementsByTagName("Email");
        assertEquals(1, emails.getLength(), "Debe haber un email");
        assertEquals(EMAIL, emails.item(0).getTextContent(), "El email debe ser correcto");
    }
    
    // Método auxiliar para extraer texto de un elemento
    private String getElementText(org.w3c.dom.Element parent, String tagName) {
        var nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
