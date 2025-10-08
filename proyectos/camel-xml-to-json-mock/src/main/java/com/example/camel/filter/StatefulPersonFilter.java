package com.example.camel.filter;

import com.example.camel.model.Person;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Bean de filtros avanzados con estado y estadísticas.
 * Demuestra filtros que mantienen contadores y lógica de negocio compleja.
 */
@Component
public class StatefulPersonFilter {
    
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicInteger adultCount = new AtomicInteger(0);
    private final AtomicLong totalAge = new AtomicLong(0);
    
    /**
     * Filtro que procesa solo cada N elementos (throttling)
     * @param person el objeto Person
     * @return true cada 3 elementos procesados
     */
    public boolean everyThirdPerson(Person person) {
        int count = processedCount.incrementAndGet();
        return count % 3 == 0;
    }
    
    /**
     * Filtro que acepta personas mayores que la edad promedio procesada
     * @param person el objeto Person
     * @return true si es mayor que el promedio actual
     */
    public boolean isAboveAverageAge(Person person) {
        if (person == null || person.getAge() == null) {
            return false;
        }
        
        int count = processedCount.get();
        if (count == 0) {
            // Primeros elementos siempre pasan
            updateStats(person);
            return true;
        }
        
        double avgAge = (double) totalAge.get() / count;
        boolean result = person.getAge() > avgAge;
        
        updateStats(person);
        return result;
    }
    
    /**
     * Filtro con límite de procesamiento
     * @param person el objeto Person
     * @return true si no se ha excedido el límite de adultos (ej: máximo 100)
     */
    public boolean withinAdultLimit(Person person) {
        if (person == null || person.getAge() == null || person.getAge() < 18) {
            return true; // Los menores siempre pasan
        }
        
        int currentAdults = adultCount.get();
        if (currentAdults >= 100) {
            return false; // Límite alcanzado
        }
        
        adultCount.incrementAndGet();
        return true;
    }
    
    /**
     * Filtro basado en patrón de distribución
     * Acepta elementos para mantener una distribución equilibrada por edad
     * @param person el objeto Person
     * @return true si contribuye al equilibrio
     */
    public boolean maintainAgeDistribution(Person person) {
        if (person == null || person.getAge() == null) {
            return false;
        }
        
        int age = person.getAge();
        int count = processedCount.get();
        
        // Estrategia simple: aceptar más jóvenes al principio, más mayores después
        if (count < 50) {
            return age < 40; // Favorecer jóvenes inicialmente
        } else {
            return age >= 40; // Luego favorecer mayores
        }
    }
    
    /**
     * Resetea los contadores (útil para testing o batch processing)
     */
    public void resetCounters() {
        processedCount.set(0);
        adultCount.set(0);
        totalAge.set(0);
    }
    
    /**
     * Obtiene estadísticas actuales
     * @return información del estado actual
     */
    public String getStats() {
        int count = processedCount.get();
        double avgAge = count > 0 ? (double) totalAge.get() / count : 0;
        
        return String.format("Processed: %d, Adults: %d, Avg Age: %.1f", 
                           count, adultCount.get(), avgAge);
    }
    
    private void updateStats(Person person) {
        if (person != null && person.getAge() != null) {
            totalAge.addAndGet(person.getAge());
            if (person.getAge() >= 18) {
                adultCount.incrementAndGet();
            }
        }
    }
    
    /**
     * Filtro de validación compleja con logging
     * @param person el objeto Person
     * @return true si pasa todas las validaciones
     */
    public boolean complexValidation(Person person) {
        if (person == null) {
            logValidation("Person is null");
            return false;
        }
        
        if (person.getId() == null || person.getId().trim().isEmpty()) {
            logValidation("Invalid ID for person: " + person.getName());
            return false;
        }
        
        if (person.getName() == null || person.getName().trim().length() < 2) {
            logValidation("Invalid name for person ID: " + person.getId());
            return false;
        }
        
        if (person.getAge() == null || person.getAge() < 0 || person.getAge() > 150) {
            logValidation("Invalid age for person: " + person.getName());
            return false;
        }
        
        logValidation("Person validated successfully: " + person.getName());
        return true;
    }
    
    private void logValidation(String message) {
        // En un entorno real, usarías un logger apropiado
        System.out.println("[FILTER] " + message);
    }
}