package com.example.camel.processor;

import com.example.camel.model.Person;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para PersonDataProcessor.
 * 
 * Prueba la lógica de procesamiento sin necesidad de levantar
 * el contexto completo de Spring o Camel.
 */
class PersonDataProcessorTest {

  @Test
  void shouldTrimPersonName() throws Exception {
    // Given - Preparar datos
    var camelContext = new DefaultCamelContext();
    var exchange = new DefaultExchange(camelContext);
    var inputPerson = Person.create("123", "  Ada Lovelace  ", 36);  // Con espacios
    exchange.getIn().setBody(inputPerson);

    // When - Ejecutar procesador
    var processor = new PersonDataProcessor();
    processor.process(exchange);

    // Then - Verificar resultado
    var processedPerson = exchange.getMessage().getBody(Person.class);
    assertNotNull(processedPerson, "El resultado no debería ser null");
    assertEquals("Ada Lovelace", processedPerson.getName(), "El nombre debería estar sin espacios");
    assertEquals("123", processedPerson.getId(), "El ID debería mantenerse igual");
    assertEquals(36, processedPerson.getAge(), "La edad debería mantenerse igual");
  }

  @Test
  void shouldHandleNullName() throws Exception {
    // Given - Persona sin nombre
    var camelContext = new DefaultCamelContext();
    var exchange = new DefaultExchange(camelContext);
    var inputPerson = Person.create();
    inputPerson.setId("456");
    inputPerson.setName(null);
    inputPerson.setAge(25);
    exchange.getIn().setBody(inputPerson);

    // When - Ejecutar procesador
    var processor = new PersonDataProcessor();
    processor.process(exchange);

    // Then - No debería fallar
    var processedPerson = exchange.getMessage().getBody(Person.class);
    assertNotNull(processedPerson);
    assertNull(processedPerson.getName(), "El nombre null debería mantenerse como null");
  }
}
