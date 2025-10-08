package com.example.camel.mapper;

import com.example.camel.model.Person;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper usando MapStruct para transformaciones automáticas entre modelos.
 * MapStruct genera el código de implementación en tiempo de compilación.
 * 
 * Ventajas:
 * - Generación automática de código
 * - Verificación en tiempo de compilación
 * - Alto rendimiento (sin reflection)
 * - Mapeos personalizados flexibles
 */
@Mapper(componentModel = "spring")
public interface PersonMapper {
    
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
    
    /**
     * Convierte Person a PersonEntity
     * Mapeo directo de campos con mismo nombre
     * Los campos de auditoría se inicializan en el constructor
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true) 
    @Mapping(target = "status", ignore = true)
    PersonEntity personToEntity(Person person);
    
    /**
     * Convierte PersonEntity a Person
     * Mapeo inverso para leer desde base de datos
     */
    Person entityToPerson(PersonEntity entity);
    
    /**
     * Convierte Person a PersonSummary
     * Usa mapeos personalizados para transformar los datos
     */
    @Mapping(source = "id", target = "identifier")
    @Mapping(source = "name", target = "fullName")
    @Mapping(source = "age", target = "ageGroup", qualifiedByName = "ageToAgeGroup")
    PersonSummary personToSummary(Person person);
    
    /**
     * Método personalizado para convertir edad numérica a grupo etario
     */
    @Named("ageToAgeGroup")
    default String ageToAgeGroup(Integer age) {
        if (age == null) {
            return "Unknown";
        }
        if (age < 18) {
            return "Minor";
        } else if (age < 30) {
            return "Young Adult";
        } else if (age < 50) {
            return "Adult";
        } else if (age < 65) {
            return "Middle Age";
        } else {
            return "Senior";
        }
    }
    
    /**
     * Mapeo con transformación personalizada del nombre
     */
    @Mapping(source = "name", target = "name", qualifiedByName = "formatName")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true) 
    @Mapping(target = "status", ignore = true)
    PersonEntity personToEntityWithFormatting(Person person);
    
    @Named("formatName")
    default String formatName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Unknown Person";
        }
        return name.trim().toUpperCase();
    }
}