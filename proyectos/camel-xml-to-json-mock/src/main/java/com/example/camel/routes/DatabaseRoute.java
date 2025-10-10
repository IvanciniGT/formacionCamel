package com.example.camel.routes;

import com.example.camel.model.PersonEntity;
import com.example.camel.service.PersonService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Rutas Camel que interactúan con la base de datos.
 * 
 * Demuestra diferentes formas de leer/escribir datos con JPA:
 * 1. Usando JPA Consumer (polling automático)
 * 2. Usando Service beans
 * 3. Usando repositorios directamente
 * 4. Combinación con procesamiento de archivos
 */
@Component
public class DatabaseRoute extends RouteBuilder {
    
    @Autowired
    private PersonService personService;
    
    @Override
    public void configure() {
        
        // =============== RUTA 1: LEER DE BD CON JPA CONSUMER ===============
        /**
         * JPA Consumer: Polling automático cada 5 segundos
         * Lee todas las PersonEntity de la BD y las procesa
         */
        from("jpa:com.example.camel.model.PersonEntity?" +
             "consumeDelete=false&" +           // No eliminar después de leer
             "consumeLockEntity=false&" +       // No bloquear entidad
             "maximumResults=100&" +            // Máximo 100 registros por poll
             "delay=5000&" +                    // Polling cada 5 segundos
             "initialDelay=3000")               // Primer poll después de 3 segundos
            .routeId("jpa-polling-route")
            .log("🔄 JPA Poll: Leyendo personas de la BD")
            .split(body())                      // Dividir lista en mensajes individuales
                .log("📋 Persona desde BD: ${body}")
                .marshal().json(JsonLibrary.Jackson)
                .log("💾 JSON: ${body}")
            .end();
        
        // =============== RUTA 2: LEER TODAS LAS PERSONAS CON SERVICE ===============
        from("timer:load-persons?period=10000&delay=1000")
            .routeId("load-all-persons")
            .log("📚 Cargando todas las personas de la BD")
            .bean(personService, "findAll")
            .log("✅ Total personas: ${body.size}")
            .split(body())
                .log("👤 Persona: ${body.name} - ${body.age} años")
            .end();
        
        // =============== RUTA 3: BUSCAR ADULTOS ===============
        from("direct:find-adults")
            .routeId("find-adults")
            .log("🔍 Buscando adultos en la BD")
            .bean(personService, "findAdults")
            .log("✅ Adultos encontrados: ${body.size}")
            .marshal().json(JsonLibrary.Jackson)
            .log("📊 JSON de adultos: ${body}");
        
        // =============== RUTA 4: BUSCAR POR RANGO DE EDAD ===============
        from("direct:find-by-age-range")
            .routeId("find-by-age-range")
            .log("🔍 Buscando por rango de edad: ${header.minAge} - ${header.maxAge}")
            .bean(personService, "findByAgeRange(${header.minAge}, ${header.maxAge})")
            .log("✅ Personas encontradas: ${body.size}")
            .marshal().json(JsonLibrary.Jackson);
        
        // =============== RUTA 5: BUSCAR POR NOMBRE ===============
        from("direct:search-by-name")
            .routeId("search-by-name")
            .log("🔎 Buscando por nombre: ${header.searchName}")
            .bean(personService, "searchByName(${header.searchName})")
            .choice()
                .when(simple("${body.size} > 0"))
                    .log("✅ Encontradas ${body.size} personas")
                    .marshal().json(JsonLibrary.Jackson)
                .endChoice()
                .otherwise()
                    .log("⚠️  No se encontraron personas con ese nombre")
                    .setBody(constant("[]"))
            .endChoice()
            .end();
        
        // =============== RUTA 6: GUARDAR PERSONA DESDE XML A BD ===============
        from("file:input-to-db?move=done&readLock=markerFile")
            .routeId("xml-to-database")
            .log("💾 Guardando persona desde XML a BD: ${header.CamelFileName}")
            
            // 1. Parsear XML
            .unmarshal().jacksonXml(com.example.camel.model.PersonImpl.class)
            .log("📄 XML parseado: ${body}")
            
            // 2. Guardar en BD usando service
            .bean(personService, "saveOrUpdate")
            .log("✅ Persona guardada en BD con ID: ${body.id}")
            
            // 3. Generar JSON de respuesta
            .marshal().json(JsonLibrary.Jackson)
            .log("📊 Resultado: ${body}")
            
            // 4. Guardar JSON en carpeta de salida
            .to("file:output-db?fileName=${file:name.noext}-saved.json");
        
        // =============== RUTA 7: EXPORTAR BD A JSON ===============
        from("direct:export-to-json")
            .routeId("export-database-to-json")
            .log("📤 Exportando base de datos a JSON")
            
            .bean(personService, "findAll")
            .marshal().json(JsonLibrary.Jackson, true) // pretty print
            .log("📋 Total personas exportadas: ${body}")
            
            .setHeader("CamelFileName", simple("persons-export-${date:now:yyyyMMdd-HHmmss}.json"))
            .to("file:output-export")
            .log("💾 Archivo exportado: ${header.CamelFileName}");
        
        // =============== RUTA 8: ESTADÍSTICAS ===============
        from("timer:statistics?period=30000&delay=5000")
            .routeId("show-statistics")
            .log("📊 Obteniendo estadísticas de la BD")
            .bean(personService, "getStatistics")
            .log("${body}");
        
        // =============== RUTA 9: QUERY JPA PERSONALIZADA ===============
        /**
         * Ejemplo de query JPA directa desde la ruta
         * Busca personas activas mayores de cierta edad
         */
        from("jpa:com.example.camel.model.PersonEntity?" +
             "query=SELECT p FROM PersonEntity p WHERE p.age >= 18 AND p.status = 'ACTIVE'&" +
             "consumeDelete=false&" +
             "delay=15000")
            .routeId("jpa-custom-query")
            .log("🎯 Query personalizada: Adultos activos")
            .split(body())
                .log("👨‍💼 Adulto activo: ${body.name} (${body.age} años)")
            .end();
        
        // =============== RUTA 10: ACTUALIZAR ESTADO POR ID ===============
        from("direct:deactivate-person")
            .routeId("deactivate-person")
            .log("🔒 Desactivando persona con ID: ${header.personId}")
            .bean(personService, "deactivate(${header.personId})")
            .setBody(constant("Person deactivated successfully"))
            .log("✅ ${body}");
        
        // =============== RUTA 11: COMBINAR LECTURA DE BD Y ARCHIVO ===============
        /**
         * Lee personas de BD y las enriquece con datos de archivos XML
         */
        from("direct:enrich-from-files")
            .routeId("enrich-database-from-files")
            .log("🔄 Enriqueciendo personas de BD con datos de archivos")
            
            // 1. Leer todas las personas de BD
            .bean(personService, "findAll")
            .split(body())
                .log("👤 Procesando: ${body.name}")
                
                // 2. Buscar archivo XML correspondiente (por external ID)
                .setHeader("lookupFile", simple("input/${body.externalId}.xml"))
                
                // 3. Aquí podrías enriquecer con pollEnrich o contentenrichar
                .log("🔍 Buscando archivo: ${header.lookupFile}")
            .end();
    }
}