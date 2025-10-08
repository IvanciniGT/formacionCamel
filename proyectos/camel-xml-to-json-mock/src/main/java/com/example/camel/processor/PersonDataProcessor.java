package com.example.camel.processor;

import com.example.camel.model.Person;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Procesador que limpia y normaliza los datos de una persona.
 * Se encarga de hacer trim() al nombre y otras validaciones básicas.
 */
public class PersonDataProcessor implements Processor {
  
  @Override
  public void process(Exchange exchange) {
    Person person = exchange.getIn().getBody(Person.class);
    
    if (person != null) {
      // Limpiar el nombre eliminando espacios en blanco
      if (person.getName() != null) {
        person.setName(person.getName().trim());
      }
      
      // Se podrían añadir más validaciones aquí:
      // - Validar formato de ID
      // - Validar rango de edad
      // - Normalizar datos, etc.
    }
    
    exchange.getMessage().setBody(person);
  }
}
