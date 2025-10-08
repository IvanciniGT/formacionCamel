package com.example.camel.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para verificar la funcionalidad de Person y PersonBuilder
 */
class PersonTest {
    
    @Test
    void shouldCreatePersonUsingStaticFactory() {
        // When
        Person person = Person.create("123", "Ada Lovelace", 36);
        
        // Then
        assertNotNull(person);
        assertEquals("123", person.getId());
        assertEquals("Ada Lovelace", person.getName());
        assertEquals(36, person.getAge());
    }
    
    @Test
    void shouldCreatePersonUsingBuilder() {
        // When
        Person person = Person.builder()
            .id("456")
            .name("Grace Hopper")
            .age(85)
            .build();
        
        // Then
        assertNotNull(person);
        assertEquals("456", person.getId());
        assertEquals("Grace Hopper", person.getName());
        assertEquals(85, person.getAge());
    }
    
    @Test
    void shouldCreateBuilderFromExistingPerson() {
        // Given
        Person original = Person.create("789", "Marie Curie", 66);
        
        // When
        Person copy = PersonBuilder.from(original)
            .age(67)  // Cambiar solo la edad
            .build();
        
        // Then
        assertNotNull(copy);
        assertEquals("789", copy.getId());
        assertEquals("Marie Curie", copy.getName()); 
        assertEquals(67, copy.getAge());  // Edad modificada
        
        // El original no debería cambiar
        assertEquals(66, original.getAge());
    }
    
    @Test
    void shouldHandleNullValues() {
        // When
        Person person = Person.builder()
            .id(null)
            .name(null)
            .age(null)
            .build();
        
        // Then
        assertNotNull(person);
        assertNull(person.getId());
        assertNull(person.getName());
        assertNull(person.getAge());
    }
}