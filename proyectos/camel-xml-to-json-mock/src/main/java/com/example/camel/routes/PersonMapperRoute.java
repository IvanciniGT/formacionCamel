package com.example.camel.routes;

import com.example.camel.mapper.PersonManualMapper;
import com.example.camel.mapper.PersonMapper;
import com.example.camel.model.Person;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonImpl;
import com.example.camel.model.PersonSummary;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Ruta que demuestra diferentes formas de usar mappers en Camel.
 * 
 * Flujo de transformaciones:
 * 1. XML → PersonImpl (deserialización)
 * 2. PersonImpl → PersonEntity (mapper automático)
 * 3. PersonEntity → PersonSummary (mapper manual)
 * 4. PersonSummary → JSON (serialización)
 * 
 * También muestra el uso de:
 * - .transform() para transformaciones inline
 * - .convertBodyTo() para conversiones de tipo
 * - .process() con mappers personalizados
 */
@Component
public class PersonMapperRoute extends RouteBuilder {
    
    public static final String ROUTE_ID = "person-mapper-demo";
    
    @Autowired
    private PersonManualMapper manualMapper;
    
    @Override
    public void configure() {
        
        // ===================== RUTA PRINCIPAL CON MAPPERS =====================
        from("direct:mapper-demo")
            .routeId(ROUTE_ID)
            .log("🎯 Iniciando demo de mappers con: ${body}")
            
            // 1. Deserializar XML a PersonImpl
            .unmarshal().jacksonXml(PersonImpl.class)
            .log("📄 XML deserializado: ${body}")
            
            // 2. Convertir PersonImpl a PersonEntity usando MapStruct
            .transform().method(PersonMapper.class, "personToEntity")
            .log("🏢 Convertido a Entity: ${body}")
            
            // 3. Convertir PersonEntity a Person usando método manual
            .process(exchange -> {
                PersonEntity entity = exchange.getIn().getBody(PersonEntity.class);
                Person person = manualMapper.fromEntity(entity);
                exchange.getIn().setBody(person);
            })
            .log("👤 Reconvertido a Person: ${body}")
            
            // 4. Crear PersonSummary usando mapper manual
            .transform().method(PersonManualMapper.class, "toSummary")
            .log("📊 Convertido a Summary: ${body}")
            
            // 5. Serializar a JSON
            .marshal().json(JsonLibrary.Jackson)
            .log("💾 JSON final: ${body}");
        
        // ============== RUTA ALTERNATIVA CON DIFERENTES ENFOQUES ==============
        from("direct:transform-demo")
            .routeId("transform-demo")
            .log("🔄 Demo de transformaciones Camel")
            
            // Método 1: Usar .transform() con expresión simple
            .transform().simple("Procesando: ${body}")
            .log("1️⃣ Transform simple: ${body}")
            
            // Método 2: Usar .convertBodyTo() para cambio de tipo
            .setBody(constant("<?xml version='1.0'?><Person><id>999</id><name>Test User</name><age>25</age></Person>"))
            .convertBodyTo(String.class)
            .log("2️⃣ ConvertBodyTo: ${body}")
            
            // Método 3: Transformación con método de bean
            .unmarshal().jacksonXml(PersonImpl.class)
            .transform().method(PersonManualMapper.class, "enrichPerson")
            .log("3️⃣ Bean method transform: ${body}")
            
            // Método 4: Transformación inline con lambda (Java 8+)
            .process(exchange -> {
                Person person = exchange.getIn().getBody(Person.class);
                if (person != null && person.getName() != null) {
                    person.setName("ENHANCED: " + person.getName());
                }
                exchange.getIn().setBody(person);
            })
            .log("4️⃣ Inline transform: ${body}");
        
        // ================= RUTA DE MAPEO CONDICIONAL =================
        from("direct:conditional-mapping")
            .routeId("conditional-mapping")
            .log("🔀 Mapeo condicional basado en contenido")
            
            .unmarshal().jacksonXml(PersonImpl.class)
            
            // Mapeo condicional basado en la edad
            .choice()
                .when(simple("${body.age} < 18"))
                    .log("👶 Persona menor de edad")
                    .transform().method(PersonMapper.class, "personToSummary")
                    .log("📊 Mapeado a Summary para menor: ${body}")
                
                .when(simple("${body.age} >= 65"))
                    .log("👴 Persona senior")
                    .transform().method(PersonManualMapper.class, "toEntity")
                    .log("🏢 Mapeado a Entity para senior: ${body}")
                
                .otherwise()
                    .log("👥 Persona adulta")
                    .transform().method(PersonManualMapper.class, "enrichPerson")
                    .log("✨ Person enriquecido: ${body}")
            .end()
            
            .marshal().json(JsonLibrary.Jackson)
            .log("🎯 Resultado final del mapeo condicional: ${body}");
        
        // ================== RUTA DE MÚLTIPLES MAPEOS ==================
        from("direct:multiple-mappings")
            .routeId("multiple-mappings")
            .log("🎭 Demo de múltiples transformaciones en cadena")
            
            .unmarshal().jacksonXml(PersonImpl.class)
            
            // Crear múltiples versiones del mismo objeto
            .multicast()
                .to("direct:to-entity", "direct:to-summary", "direct:to-enriched");
        
        // Sub-rutas para cada tipo de mapeo
        from("direct:to-entity")
            .transform().method(PersonMapper.class, "personToEntity")
            .marshal().json(JsonLibrary.Jackson)
            .log("🏢 Entity JSON: ${body}");
        
        from("direct:to-summary")
            .transform().method(PersonManualMapper.class, "toSummary")
            .marshal().json(JsonLibrary.Jackson)
            .log("📊 Summary JSON: ${body}");
        
        from("direct:to-enriched")
            .transform().method(PersonManualMapper.class, "enrichPerson")
            .marshal().json(JsonLibrary.Jackson)
            .log("✨ Enriched JSON: ${body}");
    }
}