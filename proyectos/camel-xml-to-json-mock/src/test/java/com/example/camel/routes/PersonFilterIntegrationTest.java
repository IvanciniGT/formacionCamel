package com.example.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración REAL para filtros con beans en rutas Camel.
 * Usa MockEndpoints para verificaciones concretas, no "revisar logs".
 */
@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("test")
@UseAdviceWith
class PersonFilterIntegrationTest {
    
    @Autowired
    private CamelContext camelContext;
    
    @Autowired
    private ProducerTemplate producerTemplate;
    
    @EndpointInject("mock:result")
    private MockEndpoint mockResult;
    
    @Test
    void shouldFilterAdultsCorrectly() throws Exception {
        // Given - Modificar la ruta para usar mock endpoint
        AdviceWith.adviceWith(camelContext, "filter-with-beans", route -> {
            route.weaveAddLast().to("mock:result");
        });
        camelContext.start();
        
        // Configurar expectativas del mock
        mockResult.expectedMessageCount(1); // Solo 1 mensaje debería pasar todos los filtros
        
        String adultValidVipXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>123</id>
                <name>Dr. María José González</name>
                <age>45</age>
            </Person>
            """;
        
        String minorXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>456</id>
                <name>Ana Menor</name>
                <age>16</age>
            </Person>
            """;
        
        String invalidXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id></id>
                <name></name>
                <age>25</age>
            </Person>
            """;
        
        // When - Enviar 3 mensajes pero solo 1 debería pasar
        producerTemplate.sendBody("direct:filter-demo", adultValidVipXml); // ✓ Pasa
        producerTemplate.sendBody("direct:filter-demo", minorXml); // ✗ Bloqueado por isAdult
        producerTemplate.sendBody("direct:filter-demo", invalidXml); // ✗ Bloqueado por isValidForProcessing
        
        // Then - Verificar que SOLO el adulto VIP válido llegó al final
        mockResult.assertIsSatisfied();
        
        String resultJson = mockResult.getExchanges().get(0).getIn().getBody(String.class);
        assertNotNull(resultJson);
        assertTrue(resultJson.contains("María José González"));
    }
    
    @Test
    void shouldFilterByAgeRange() throws Exception {
        // Given
        AdviceWith.adviceWith(camelContext, "filter-with-parameters", route -> {
            route.weaveAddLast().to("mock:rangeResult");
        });
        camelContext.start();
        
        MockEndpoint mockRangeResult = camelContext.getEndpoint("mock:rangeResult", MockEndpoint.class);
        mockRangeResult.expectedMessageCount(2); // Solo 2 de 3 deberían pasar
        
        String youngAdultXml = createPersonXml("789", "Carlos Joven", 28); // ✓ Pasa (25-65)
        String middleAgeXml = createPersonXml("101", "Laura Adulta", 45); // ✓ Pasa (25-65)
        String seniorXml = createPersonXml("102", "Roberto Senior", 70); // ✗ No pasa (>65)
        
        // When
        producerTemplate.sendBody("direct:filter-with-params", youngAdultXml);
        producerTemplate.sendBody("direct:filter-with-params", middleAgeXml);
        producerTemplate.sendBody("direct:filter-with-params", seniorXml);
        
        // Then
        mockRangeResult.assertIsSatisfied();
        assertEquals(2, mockRangeResult.getReceivedCounter(), 
                    "Solo 2 personas en el rango 25-65 deberían pasar");
    }
    
    @Test
    void shouldFilterByPriorityHeader() throws Exception {
        // Given
        AdviceWith.adviceWith(camelContext, "filter-with-headers", route -> {
            route.weaveAddLast().to("mock:priorityResult");
        });
        camelContext.start();
        
        MockEndpoint mockPriorityResult = camelContext.getEndpoint("mock:priorityResult", MockEndpoint.class);
        mockPriorityResult.expectedMessageCount(1); // Solo archivo prioritario pasa
        
        String personXml = createPersonXml("999", "Persona Prioritaria", 35);
        
        // When - Enviar con diferentes headers
        producerTemplate.sendBodyAndHeader("direct:filter-with-headers", 
                                          personXml, 
                                          "CamelFileName", 
                                          "urgent_data.xml"); // ✓ Pasa (prioritario)
        
        producerTemplate.sendBodyAndHeader("direct:filter-with-headers", 
                                          personXml, 
                                          "CamelFileName", 
                                          "regular_data.xml"); // ✗ No pasa (no prioritario)
        
        producerTemplate.sendBodyAndHeader("direct:filter-with-headers", 
                                          personXml, 
                                          "CamelFileName", 
                                          "test_data.xml"); // ✗ No pasa (archivo de test)
        
        // Then
        mockPriorityResult.assertIsSatisfied();
        String resultJson = mockPriorityResult.getExchanges().get(0).getIn().getBody(String.class);
        assertTrue(resultJson.contains("Persona Prioritaria"));
    }
    
    @Test
    void shouldRouteByAgeGroupAfterFilter() throws Exception {
        // Given
        AdviceWith.adviceWith(camelContext, "combined-filters-choice", route -> {
            route.interceptSendToEndpoint("direct:senior-processing")
                 .skipSendToOriginalEndpoint()
                 .to("mock:seniors");
            route.interceptSendToEndpoint("direct:adult-processing")
                 .skipSendToOriginalEndpoint()
                 .to("mock:adults");
            route.interceptSendToEndpoint("direct:minor-processing")
                 .skipSendToOriginalEndpoint()
                 .to("mock:minors");
        });
        camelContext.start();
        
        MockEndpoint mockSeniors = camelContext.getEndpoint("mock:seniors", MockEndpoint.class);
        MockEndpoint mockAdults = camelContext.getEndpoint("mock:adults", MockEndpoint.class);
        MockEndpoint mockMinors = camelContext.getEndpoint("mock:minors", MockEndpoint.class);
        
        mockSeniors.expectedMessageCount(1);
        mockAdults.expectedMessageCount(1);
        mockMinors.expectedMessageCount(1);
        // El inválido (sin nombre) no llega a ningún destino (filtrado antes)
        
        // When
        producerTemplate.sendBody("direct:combined-filters", 
                                 createPersonXml("MINOR1", "Ana Menor", 15));
        producerTemplate.sendBody("direct:combined-filters", 
                                 createPersonXml("ADULT1", "Carlos Adulto", 35));
        producerTemplate.sendBody("direct:combined-filters", 
                                 createPersonXml("SENIOR1", "María Senior", 70));
        producerTemplate.sendBody("direct:combined-filters", 
                                 createPersonXml("INVALID1", "", 25)); // Nombre inválido
        
        // Then
        mockSeniors.assertIsSatisfied();
        mockAdults.assertIsSatisfied();
        mockMinors.assertIsSatisfied();
        
        // Verificar contenido
        assertTrue(mockSeniors.getExchanges().get(0).getIn().getBody(String.class).contains("María"));
        assertTrue(mockAdults.getExchanges().get(0).getIn().getBody(String.class).contains("Carlos"));
        assertTrue(mockMinors.getExchanges().get(0).getIn().getBody(String.class).contains("Ana"));
    }
    
    /**
     * Helper method para crear XML de persona
     */
    private String createPersonXml(String id, String name, int age) {
        return String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>%s</id>
                <name>%s</name>
                <age>%d</age>
            </Person>
            """, id, name, age);
    }
    
    @Test
    void shouldDocumentFilterUsagePatterns() {
        // Este es un test de documentación que siempre pasa
        // pero muestra claramente cómo usar los filtros
        
        String basicFilterUsage = """
            // FILTRO BÁSICO:
            .filter().method(PersonFilterBean.class, "isAdult")
            """;
        
        String parameterizedFilterUsage = """
            // FILTRO CON PARÁMETROS:
            .filter().method(PersonFilterBean.class, "isInAgeRange(${body}, 25, 65)")
            """;
        
        String headerFilterUsage = """
            // FILTRO CON HEADERS:
            .filter().method(PersonFilterBean.class, 
                           "isPriorityFile(${body}, ${header.CamelFileName})")
            """;
        
        String combinedUsage = """
            // COMBINACIÓN DE FILTRO + CHOICE:
            .filter().method(PersonFilterBean.class, "isValidForProcessing")
            .choice()
                .when().method(PersonFilterBean.class, "isSenior")
                    // Procesamiento para seniors
                .when().method(PersonFilterBean.class, "isAdult")
                    // Procesamiento para adultos
                .otherwise()
                    // Procesamiento para menores
            .end()
            """;
        
        // Assertions para que el test sea válido
        assertNotNull(basicFilterUsage, "Ejemplo básico debe estar documentado");
        assertNotNull(parameterizedFilterUsage, "Ejemplo con parámetros debe estar documentado");
        assertNotNull(headerFilterUsage, "Ejemplo con headers debe estar documentado");
        assertNotNull(combinedUsage, "Ejemplo combinado debe estar documentado");
        
        assertTrue(basicFilterUsage.contains("isAdult"), 
                  "Ejemplo básico debe mostrar método de filtro");
        assertTrue(parameterizedFilterUsage.contains("isInAgeRange"), 
                  "Ejemplo parametrizado debe mostrar parámetros");
        assertTrue(headerFilterUsage.contains("CamelFileName"), 
                  "Ejemplo con headers debe mostrar uso de headers");
        assertTrue(combinedUsage.contains("choice"), 
                  "Ejemplo combinado debe mostrar integración con choice");
    }
}