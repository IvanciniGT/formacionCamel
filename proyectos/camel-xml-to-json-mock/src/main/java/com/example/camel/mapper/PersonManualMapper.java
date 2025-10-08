package com.example.camel.mapper;

import com.example.camel.model.Person;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonSummary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper manual tradicional para transformaciones personalizadas.
 * 
 * Ventajas:
 * - Control total sobre la lógica de mapeo
 * - Fácil debugging y mantenimiento
 * - Lógica de negocio compleja
 * - No dependencias externas
 */
@Component
public class PersonManualMapper {
    
    /**
     * Convierte Person a PersonEntity con lógica personalizada
     */
    public PersonEntity toEntity(Person person) {
        if (person == null) {
            return null;
        }
        
        PersonEntity entity = new PersonEntity();
        entity.setId(person.getId());
        entity.setName(cleanAndFormatName(person.getName()));
        entity.setAge(person.getAge());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setStatus(determineStatus(person));
        
        return entity;
    }
    
    /**
     * Convierte PersonEntity a Person
     */
    public Person fromEntity(PersonEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Person.create(
            entity.getId(),
            entity.getName(),
            entity.getAge()
        );
    }
    
    /**
     * Convierte Person a PersonSummary con lógica de negocio
     */
    public PersonSummary toSummary(Person person) {
        if (person == null) {
            return null;
        }
        
        return new PersonSummary(
            formatIdentifier(person.getId()),
            formatFullName(person.getName()),
            calculateAgeGroup(person.getAge())
        );
    }
    
    /**
     * Crea una copia enriquecida de Person con datos adicionales
     */
    public Person enrichPerson(Person original) {
        if (original == null) {
            return null;
        }
        
        return Person.builder()
            .id(original.getId())
            .name(enhanceName(original.getName()))
            .age(normalizeAge(original.getAge()))
            .build();
    }
    
    // ================ MÉTODOS PRIVADOS DE TRANSFORMACIÓN ================
    
    private String cleanAndFormatName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "UNKNOWN";
        }
        
        // Limpiar caracteres especiales, convertir a title case
        String cleaned = name.trim()
                            .replaceAll("[^a-zA-Z\\s]", "")
                            .replaceAll("\\s+", " ")
                            .toLowerCase();
        
        // Convertir a Title Case manualmente
        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
    
    private String determineStatus(Person person) {
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            return "INCOMPLETE";
        }
        if (person.getAge() == null || person.getAge() < 0) {
            return "INVALID";
        }
        return "ACTIVE";
    }
    
    private String formatIdentifier(String id) {
        if (id == null) {
            return "NO-ID";
        }
        return "P-" + id.toUpperCase();
    }
    
    private String formatFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Anonymous Person";
        }
        return name.trim();
    }
    
    private String calculateAgeGroup(Integer age) {
        if (age == null) {
            return "Age Unknown";
        }
        
        return switch (age / 10) {
            case 0, 1 -> "Child (" + age + " years)";
            case 2 -> "Young Adult (" + age + " years)";
            case 3, 4 -> "Adult (" + age + " years)";
            case 5, 6 -> "Middle Age (" + age + " years)";
            default -> age >= 70 ? "Senior (" + age + " years)" : "Adult (" + age + " years)";
        };
    }
    
    private String enhanceName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Enhanced Unknown Person";
        }
        
        // Agregar título honorífico basado en longitud del nombre (ejemplo simple)
        String cleanName = name.trim();
        if (cleanName.split("\\s+").length > 2) {
            return "Dr. " + cleanName;
        } else {
            return "Mr./Ms. " + cleanName;
        }
    }
    
    private Integer normalizeAge(Integer age) {
        if (age == null) {
            return 0;
        }
        if (age < 0) {
            return 0;
        }
        if (age > 150) {
            return 150;
        }
        return age;
    }
}