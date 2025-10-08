package com.example.camel.filter;

import com.example.camel.model.Person;
import org.springframework.stereotype.Component;

/**
 * Bean de filtros para validaciones y criterios de procesamiento.
 * Contiene métodos que devuelven boolean para usar en .filter() de Camel.
 * 
 * Los métodos pueden usar diferentes tipos de parámetros:
 * - Person: el objeto actual del body
 * - Exchange: acceso completo al contexto
 * - Headers: para leer headers específicos
 */
@Component
public class PersonFilterBean {
    
    /**
     * Filtra personas adultas (>= 18 años)
     * @param person el objeto Person del body
     * @return true si es adulto, false en caso contrario
     */
    public boolean isAdult(Person person) {
        if (person == null || person.getAge() == null) {
            return false;
        }
        return person.getAge() >= 18;
    }
    
    /**
     * Filtra personas senior (>= 65 años)
     * @param person el objeto Person del body
     * @return true si es senior, false en caso contrario
     */
    public boolean isSenior(Person person) {
        if (person == null || person.getAge() == null) {
            return false;
        }
        return person.getAge() >= 65;
    }
    
    /**
     * Filtra personas con nombre válido (no null, no vacío)
     * @param person el objeto Person del body
     * @return true si tiene nombre válido
     */
    public boolean hasValidName(Person person) {
        return person != null && 
               person.getName() != null && 
               !person.getName().trim().isEmpty();
    }
    
    /**
     * Filtra personas con ID válido
     * @param person el objeto Person del body
     * @return true si tiene ID válido
     */
    public boolean hasValidId(Person person) {
        return person != null && 
               person.getId() != null && 
               !person.getId().trim().isEmpty();
    }
    
    /**
     * Filtra personas VIP (nombre largo como criterio de ejemplo)
     * @param person el objeto Person del body
     * @return true si es considerado VIP
     */
    public boolean isVIP(Person person) {
        if (person == null || person.getName() == null) {
            return false;
        }
        
        String name = person.getName().trim();
        // Criterios VIP: nombre con más de 2 palabras O contiene "Dr." o "Prof."
        return name.split("\\s+").length > 2 || 
               name.toLowerCase().contains("dr.") ||
               name.toLowerCase().contains("prof.");
    }
    
    /**
     * Filtra personas por rango de edad
     * @param person el objeto Person del body
     * @param minAge edad mínima (inclusive)
     * @param maxAge edad máxima (inclusive)
     * @return true si está en el rango
     */
    public boolean isInAgeRange(Person person, int minAge, int maxAge) {
        if (person == null || person.getAge() == null) {
            return false;
        }
        
        int age = person.getAge();
        return age >= minAge && age <= maxAge;
    }
    
    /**
     * Filtro compuesto: persona válida para procesamiento
     * Combina múltiples validaciones
     * @param person el objeto Person del body
     * @return true si cumple todos los criterios básicos
     */
    public boolean isValidForProcessing(Person person) {
        return hasValidName(person) && 
               hasValidId(person) && 
               person.getAge() != null && 
               person.getAge() >= 0 && 
               person.getAge() <= 150;
    }
    
    /**
     * Filtro usando información del header del archivo
     * @param person el objeto Person del body
     * @param fileName nombre del archivo desde header
     * @return true si el archivo es de tipo prioritario
     */
    public boolean isPriorityFile(Person person, String fileName) {
        if (fileName == null) {
            return false;
        }
        
        // Archivos prioritarios: contienen "urgent", "priority" o "vip"
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.contains("urgent") || 
               lowerFileName.contains("priority") || 
               lowerFileName.contains("vip");
    }
    
    /**
     * Filtro por patrón en el nombre
     * @param person el objeto Person del body
     * @param pattern patrón a buscar (case insensitive)
     * @return true si el nombre contiene el patrón
     */
    public boolean nameContains(Person person, String pattern) {
        if (person == null || person.getName() == null || pattern == null) {
            return false;
        }
        
        return person.getName().toLowerCase().contains(pattern.toLowerCase());
    }
    
    /**
     * Filtro usando el Exchange completo
     * Ejemplo de acceso a headers, properties, etc.
     * @param exchange el exchange completo de Camel
     * @return true si cumple criterios complejos
     */
    public boolean isValidExchange(org.apache.camel.Exchange exchange) {
        Person person = exchange.getIn().getBody(Person.class);
        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
        
        // Lógica compleja usando múltiples fuentes de información
        boolean hasValidPerson = isValidForProcessing(person);
        boolean hasValidFile = fileName != null && fileName.endsWith(".xml");
        boolean isNotTestFile = fileName == null || !fileName.contains("test");
        
        return hasValidPerson && hasValidFile && isNotTestFile;
    }
}