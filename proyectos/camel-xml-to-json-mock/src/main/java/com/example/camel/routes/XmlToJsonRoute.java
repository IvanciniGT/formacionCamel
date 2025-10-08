package com.example.camel.routes;

import com.example.camel.model.Person;
import com.example.camel.processor.PersonDataProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Ruta Camel que transforma archivos XML de personas a formato JSON.
 * 
 * Flujo:
 * 1. Leer archivos XML de la carpeta 'input'
 * 2. Parsear XML a objeto Person
 * 3. Procesar/limpiar datos con PersonDataProcessor
 * 4. Serializar a JSON
 * 5. Guardar en carpeta 'output'
 */
@Component
public class XmlToJsonTransformRoute extends RouteBuilder {

  public static final String ROUTE_ID = "xml-to-json-transform";
  public static final String INPUT_ENDPOINT = "file:input?move=done&readLock=markerFile";
  public static final String OUTPUT_ENDPOINT = "file:output?fileName=${file:name.noext}.json";

  @Override
  public void configure() {
    from(INPUT_ENDPOINT)
      .routeId(ROUTE_ID)
      .log("Procesando archivo XML: ${header.CamelFileName}")
      .unmarshal().jacksonXml(Person.class)
      .log("XML parseado correctamente: ${body}")
      .process(new PersonDataProcessor())
      .log("Datos procesados y limpiados")
      .marshal().json(JsonLibrary.Jackson)
      .log("JSON generado: ${body}")
      .to(OUTPUT_ENDPOINT)
      .log("Archivo guardado como: ${header.CamelFileName.replaceAll('\\.xml$', '.json')}");
  }
}
