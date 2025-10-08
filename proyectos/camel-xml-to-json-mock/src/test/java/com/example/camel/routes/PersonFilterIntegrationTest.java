package com.example.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de integración para demostrar filtros con beans en rutas Camel.
 * 
 * IMPORTANTE: Para ejecutar estos tests, asegúrate de tener archivos XML
 * de ejemplo en las carpetas correspondientes o usar ProducerTemplate
 * para enviar datos directamente a las rutas.
 */
@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("test")
class PersonFilterIntegrationTest {
    
    @Autowired
    private CamelContext camelContext;
    
    @Autowired
    private ProducerTemplate producerTemplate;
    
    @Test
    void shouldDemonstrateBasicFilters() throws Exception {
        // Given - XML de ejemplo con diferentes tipos de personas
        String adultValidXml = """
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
        
        // When - Enviar a la ruta de filtros básicos
        System.out.println("\\n=== TEST: Filtros Básicos ===");
        
        // El adulto VIP válido debería pasar todos los filtros
        producerTemplate.sendBody("direct:filter-demo", adultValidXml);
        
        // El menor no debería pasar el filtro de adultos
        producerTemplate.sendBody("direct:filter-demo", minorXml);
        
        // La persona inválida no debería pasar el primer filtro
        producerTemplate.sendBody("direct:filter-demo", invalidXml);
        
        // Then - Verificar en los logs que solo el adulto VIP llegó al final
        Thread.sleep(1000); // Dar tiempo para procesamiento
    }
    
    @Test
    void shouldDemonstrateParameterizedFilters() throws Exception {
        // Given - Personas en diferentes rangos de edad
        String youngAdultXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>789</id>
                <name>Carlos Joven</name>
                <age>28</age>
            </Person>
            """;
        
        String middleAgeXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>101</id>
                <name>Laura Adulta</name>
                <age>45</age>
            </Person>
            """;
        
        String seniorXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>102</id>
                <name>Roberto Senior</name>
                <age>70</age>
            </Person>
            """;
        
        // When - Enviar a filtros con parámetros (rango 25-65)
        System.out.println("\\n=== TEST: Filtros con Parámetros ===");
        
        producerTemplate.sendBody("direct:filter-with-params", youngAdultXml); // Debería pasar
        producerTemplate.sendBody("direct:filter-with-params", middleAgeXml); // Debería pasar
        producerTemplate.sendBody("direct:filter-with-params", seniorXml); // NO debería pasar (70 > 65)
        
        Thread.sleep(1000);
    }
    
    @Test
    void shouldDemonstrateHeaderBasedFilters() throws Exception {
        // Given - XML con headers de archivo
        String personXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Person>
                <id>999</id>
                <name>Persona Prioritaria</name>
                <age>35</age>
            </Person>
            """;
        
        // When - Enviar con diferentes headers de archivo
        System.out.println("\\n=== TEST: Filtros con Headers ===");
        
        // Archivo prioritario - debería pasar
        producerTemplate.sendBodyAndHeader("direct:filter-with-headers", 
                                          personXml, 
                                          "CamelFileName", 
                                          "urgent_data.xml");
        
        // Archivo regular - NO debería pasar
        producerTemplate.sendBodyAndHeader("direct:filter-with-headers", 
                                          personXml, 
                                          "CamelFileName", 
                                          "regular_data.xml");
        
        // Archivo de test - NO debería pasar por el filtro de Exchange
        producerTemplate.sendBodyAndHeader("direct:filter-with-headers", 
                                          personXml, 
                                          "CamelFileName", 
                                          "test_data.xml");
        
        Thread.sleep(1000);
    }
    
    @Test
    void shouldDemonstrateStatefulFilters() throws Exception {
        // Given - Múltiples personas para probar filtros con estado
        String[] persons = {
            createPersonXml("P1", "Persona Uno", 20),
            createPersonXml("P2", "Persona Dos", 25),
            createPersonXml("P3", "Persona Tres", 30),
            createPersonXml("P4", "Persona Cuatro", 35),
            createPersonXml("P5", "Persona Cinco", 40),
            createPersonXml("P6", "Persona Seis", 45)
        };
        
        // When - Enviar múltiples personas a filtros con estado
        System.out.println("\\n=== TEST: Filtros con Estado ===");
        
        for (String personXml : persons) {
            producerTemplate.sendBody("direct:stateful-filters", personXml);
            Thread.sleep(100); // Pequeña pausa entre envíos
        }
        
        Thread.sleep(2000); // Tiempo para ver todos los resultados
    }
    
    @Test
    void shouldDemonstrateCombinedFiltersAndChoice() throws Exception {
        // Given - Personas de diferentes edades
        String[] testPersons = {
            createPersonXml("MINOR1", "Ana Menor", 15),
            createPersonXml("ADULT1", "Carlos Adulto", 35),
            createPersonXml("SENIOR1", "María Senior", 70),
            createPersonXml("INVALID1", "", 25) // Nombre inválido
        };
        
        // When - Enviar a la ruta combinada
        System.out.println("\\n=== TEST: Filtros Combinados + Choice ===");
        
        for (String personXml : testPersons) {
            producerTemplate.sendBody("direct:combined-filters", personXml);
            Thread.sleep(200);
        }
        
        Thread.sleep(1000);
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
    void shouldShowFilterUsageExamples() {
        System.out.println("""
            
            ═══════════════════════════════════════════════════════════════
                        EJEMPLOS DE USO DE FILTROS CON BEANS
            ═══════════════════════════════════════════════════════════════
            
            1. FILTRO BÁSICO:
               .filter().method(PersonFilterBean.class, "isAdult")
            
            2. FILTRO CON PARÁMETROS:
               .filter().method(PersonFilterBean.class, "isInAgeRange(${body}, 25, 65)")
            
            3. FILTRO CON HEADERS:
               .filter().method(PersonFilterBean.class, "isPriorityFile(${body}, ${header.CamelFileName})")
            
            4. FILTRO CON EXCHANGE COMPLETO:
               .filter().method(PersonFilterBean.class, "isValidExchange")
            
            5. FILTRO CON ESTADO:
               .filter().method(StatefulPersonFilter.class, "everyThirdPerson")
            
            6. COMBINADO CON CHOICE:
               .filter().method(PersonFilterBean.class, "isValidForProcessing")
               .choice()
                   .when().method(PersonFilterBean.class, "isSenior")
                       // Procesamiento para seniors
                   .when().method(PersonFilterBean.class, "isAdult")
                       // Procesamiento para adultos
                   .otherwise()
                       // Procesamiento para menores
               .end()
            
            ═══════════════════════════════════════════════════════════════
            """);
    }
}