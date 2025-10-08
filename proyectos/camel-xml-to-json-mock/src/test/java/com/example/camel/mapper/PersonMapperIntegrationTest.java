package com.example.camel.mapper;

import com.example.camel.model.Person;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonSummary;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para los mappers.
 * Demuestra el uso de MapStruct y mappers manuales.
 */
@SpringBootTest
@ActiveProfiles("test")
class PersonMapperIntegrationTest {
    
    private final PersonManualMapper manualMapper = new PersonManualMapper();
    
    @Test
    void shouldMapPersonToEntityUsingMapStruct() {
        // Given
        Person person = Person.create("123", "Ada Lovelace", 36);
        
        // When
        PersonEntity entity = PersonMapper.INSTANCE.personToEntity(person);
        
        // Then
        assertNotNull(entity);
        assertEquals("123", entity.getId());
        assertEquals("Ada Lovelace", entity.getName());
        assertEquals(36, entity.getAge());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals("ACTIVE", entity.getStatus()); // Del constructor
    }
    
    @Test
    void shouldMapPersonToSummaryUsingMapStruct() {
        // Given
        Person person = Person.create("456", "Grace Hopper", 85);
        
        // When
        PersonSummary summary = PersonMapper.INSTANCE.personToSummary(person);
        
        // Then
        assertNotNull(summary);
        assertEquals("456", summary.getIdentifier());
        assertEquals("Grace Hopper", summary.getFullName());
        assertEquals("Senior", summary.getAgeGroup()); // 85 años = Senior
    }
    
    @Test
    void shouldMapPersonToEntityUsingManualMapper() {
        // Given
        Person person = Person.create("789", "  marie curie  ", 66);
        
        // When
        PersonEntity entity = manualMapper.toEntity(person);
        
        // Then
        assertNotNull(entity);
        assertEquals("789", entity.getId());
        assertEquals("Marie Curie", entity.getName()); // Formateado a Title Case
        assertEquals(66, entity.getAge());
        assertEquals("ACTIVE", entity.getStatus());
    }
    
    @Test
    void shouldEnrichPersonWithManualMapper() {
        // Given
        Person original = Person.create("999", "john doe", 30);
        
        // When  
        Person enriched = manualMapper.enrichPerson(original);
        
        // Then
        assertNotNull(enriched);
        assertEquals("999", enriched.getId());
        assertEquals("Mr./Ms. john doe", enriched.getName()); // Con título
        assertEquals(30, enriched.getAge());
    }
    
    @Test
    void shouldHandleEdgeCasesInMappers() {
        // Given - Person con datos problemáticos
        Person person = Person.create(null, "   ", -5);
        
        // When - MapStruct
        PersonSummary summary = PersonMapper.INSTANCE.personToSummary(person);
        
        // Then
        assertNotNull(summary);
        assertNull(summary.getIdentifier()); // null se mantiene
        assertEquals("   ", summary.getFullName()); // MapStruct no limpia
        assertEquals("Unknown", summary.getAgeGroup()); // Edad negativa = Unknown
        
        // When - Manual mapper (más robusto)
        PersonEntity entity = manualMapper.toEntity(person);
        
        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("UNKNOWN", entity.getName()); // Limpiado por mapper manual
        assertEquals(-5, entity.getAge()); // Se mantiene el valor original
        assertEquals("INCOMPLETE", entity.getStatus()); // Estado calculado
    }
    
    @Test
    void shouldCalculateAgeGroupsCorrectly() {
        // Test de diferentes grupos etarios
        assertEquals("Minor", PersonMapper.INSTANCE.ageToAgeGroup(15));
        assertEquals("Young Adult", PersonMapper.INSTANCE.ageToAgeGroup(25));
        assertEquals("Adult", PersonMapper.INSTANCE.ageToAgeGroup(35));
        assertEquals("Middle Age", PersonMapper.INSTANCE.ageToAgeGroup(55));
        assertEquals("Senior", PersonMapper.INSTANCE.ageToAgeGroup(70));
    }
    
    @Test
    void shouldHandleNullValues() {
        // Given
        Person nullPerson = null;
        
        // When/Then - Mappers deben manejar nulls sin explotar
        assertNull(PersonMapper.INSTANCE.personToEntity(nullPerson));
        assertNull(PersonMapper.INSTANCE.personToSummary(nullPerson));
        assertNull(manualMapper.toEntity(nullPerson));
        assertNull(manualMapper.toSummary(nullPerson));
        assertNull(manualMapper.enrichPerson(nullPerson));
    }
    
    @Test
    void shouldRoundTripMappingWork() {
        // Given - Person original
        Person original = Person.create("round-trip", "Test Person", 42);
        
        // When - Person → Entity → Person
        PersonEntity entity = PersonMapper.INSTANCE.personToEntity(original);
        Person roundTrip = PersonMapper.INSTANCE.entityToPerson(entity);
        
        // Then - Datos básicos deben mantenerse
        assertEquals(original.getId(), roundTrip.getId());
        assertEquals(original.getName(), roundTrip.getName());
        assertEquals(original.getAge(), roundTrip.getAge());
    }
}