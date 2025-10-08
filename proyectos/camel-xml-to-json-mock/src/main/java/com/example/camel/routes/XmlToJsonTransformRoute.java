package com.example.camel.routes;

import com.example.camel.mapper.PersonManualMapper;
import com.example.camel.mapper.PersonMapper;
import com.example.camel.model.PersonImpl;
import com.example.camel.processor.PersonDataProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Ruta Camel que transforma archivos XML de personas a formato JSON.
 * 
 * Flujo:
 * 1. Leer archivos XML de la carpeta configurada ({{app.input.path}})
 * 2. Parsear XML a objeto Person usando Jackson XML
 * 3. Procesar/limpiar datos con PersonDataProcessor
 * 4. Serializar a JSON usando Jackson
 * 5. Guardar en carpeta configurada ({{app.output.path}})
 * 
 * Los endpoints son configurables vía application.yml
 */
@Component
public class XmlToJsonTransformRoute extends RouteBuilder {

  public static final String ROUTE_ID = "xml-to-json-transform";

  @Override
  public void configure() {
    
    // =================== RUTA ORIGINAL (SIN MAPPERS) ===================
    from("{{app.input.path}}")
      .routeId(ROUTE_ID)
      .log("📁 Procesando archivo XML: ${header.CamelFileName}")
      .unmarshal().jacksonXml(PersonImpl.class)
      .log("✅ XML parseado correctamente: ${body}")
      .process(new PersonDataProcessor())
      .log("🔧 Datos procesados y limpiados")
      .marshal().json(JsonLibrary.Jackson)
      .log("💾 JSON generado: ${body}")
      .to("{{app.output.path}}")
      .log("🎉 Archivo guardado como: ${header.CamelFileName.replaceAll('\\.xml$', '.json')}");
    
    // ================= RUTA ALTERNATIVA CON MAPPERS ===================
    // Para activar esta ruta, cambia el endpoint de entrada por: "file:input-mapper?..."
    from("file:input-mapper?move=done&readLock=markerFile")
      .routeId("xml-to-json-with-mappers")
      .log("🗺️ Procesando con mappers: ${header.CamelFileName}")
      
      // 1. Deserializar XML
      .unmarshal().jacksonXml(PersonImpl.class)
      .log("📄 XML → PersonImpl: ${body}")
      
      // 2. Procesar datos básicos
      .process(new PersonDataProcessor())
      .log("🔧 Datos limpiados: ${body}")
      
      // 3. Enriquecer con mapper manual 
      .transform().method(PersonManualMapper.class, "enrichPerson")
      .log("✨ Person enriquecido: ${body}")
      
      // 4. Convertir a Entity usando MapStruct
      .transform().method(PersonMapper.class, "personToEntity")
      .log("🏢 Convertido a Entity: ${body}")
      
      // 5. Serializar y guardar
      .marshal().json(JsonLibrary.Jackson)
      .log("💾 JSON con mappers: ${body}")
      .to("file:output-mapper?fileName=${file:name.noext}-mapped.json")
      .log("🎯 Archivo con mappers guardado");
  }
}
