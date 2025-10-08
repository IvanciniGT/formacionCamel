package com.example.camel.filter;

import com.example.camel.model.Person;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para los beans de filtrado.
 * Demuestra cómo funcionan los filtros en diferentes escenarios.
 */
class PersonFilterBeanTest {
    
    private PersonFilterBean filterBean;
    private StatefulPersonFilter statefulFilter;
    
    @BeforeEach
    void setUp() {
        filterBean = new PersonFilterBean();
        statefulFilter = new StatefulPersonFilter();
        statefulFilter.resetCounters(); // Reset para cada test
    }
    
    @Test
    void shouldFilterAdults() {
        // Given
        Person adult = Person.create("1", "Juan Adulto", 25);
        Person minor = Person.create("2", "Ana Menor", 16);
        Person nullAge = Person.create("3", "Sin Edad", null);
        
        // When/Then
        assertTrue(filterBean.isAdult(adult));
        assertFalse(filterBean.isAdult(minor));
        assertFalse(filterBean.isAdult(nullAge));
        assertFalse(filterBean.isAdult(null));
    }
    
    @Test
    void shouldFilterSeniors() {
        // Given
        Person senior = Person.create("1", "Abuelo", 70);
        Person adult = Person.create("2", "Adulto", 40);
        Person exactlyAge = Person.create("3", "Exacto", 65);
        
        // When/Then
        assertTrue(filterBean.isSenior(senior));
        assertFalse(filterBean.isSenior(adult));
        assertTrue(filterBean.isSenior(exactlyAge)); // 65 es senior
    }
    
    @Test
    void shouldValidateNames() {
        // Given
        Person validName = Person.create("1", "María García", 30);
        Person emptyName = Person.create("2", "", 30);
        Person nullName = Person.create("3", null, 30);
        Person spacesName = Person.create("4", "   ", 30);
        
        // When/Then
        assertTrue(filterBean.hasValidName(validName));
        assertFalse(filterBean.hasValidName(emptyName));
        assertFalse(filterBean.hasValidName(nullName));
        assertFalse(filterBean.hasValidName(spacesName));
    }
    
    @Test
    void shouldIdentifyVIP() {
        // Given
        Person vipLongName = Person.create("1", "Dr. María José González", 45);
        Person vipDoctor = Person.create("2", "Dr. Smith", 50);
        Person vipProfessor = Person.create("3", "Prof. Johnson", 40);
        Person regularPerson = Person.create("4", "John Doe", 35);
        
        // When/Then
        assertTrue(filterBean.isVIP(vipLongName)); // Más de 2 palabras
        assertTrue(filterBean.isVIP(vipDoctor)); // Contiene "Dr."
        assertTrue(filterBean.isVIP(vipProfessor)); // Contiene "Prof."
        assertFalse(filterBean.isVIP(regularPerson)); // Persona regular
    }
    
    @Test
    void shouldFilterByAgeRange() {
        // Given
        Person young = Person.create("1", "Joven", 20);
        Person middle = Person.create("2", "Medio", 35);
        Person old = Person.create("3", "Mayor", 50);
        
        // When/Then - Rango 25-45
        assertFalse(filterBean.isInAgeRange(young, 25, 45)); // 20 < 25
        assertTrue(filterBean.isInAgeRange(middle, 25, 45)); // 25 <= 35 <= 45
        assertFalse(filterBean.isInAgeRange(old, 25, 45)); // 50 > 45
    }
    
    @Test
    void shouldValidateForProcessing() {
        // Given
        Person valid = Person.create("123", "Valid Person", 30);
        Person invalidId = Person.create("", "Valid Name", 30);
        Person invalidName = Person.create("123", "", 30);
        Person invalidAge = Person.create("123", "Valid Name", -5);
        Person tooOld = Person.create("123", "Valid Name", 200);
        
        // When/Then
        assertTrue(filterBean.isValidForProcessing(valid));
        assertFalse(filterBean.isValidForProcessing(invalidId));
        assertFalse(filterBean.isValidForProcessing(invalidName));
        assertFalse(filterBean.isValidForProcessing(invalidAge));
        assertFalse(filterBean.isValidForProcessing(tooOld));
    }
    
    @Test
    void shouldFilterPriorityFiles() {
        // Given
        Person person = Person.create("1", "Test", 30);
        
        // When/Then
        assertTrue(filterBean.isPriorityFile(person, "urgent_data.xml"));
        assertTrue(filterBean.isPriorityFile(person, "PRIORITY-person.xml"));
        assertTrue(filterBean.isPriorityFile(person, "vip_clients.xml"));
        assertFalse(filterBean.isPriorityFile(person, "regular_data.xml"));
        assertFalse(filterBean.isPriorityFile(person, null));
    }
    
    @Test
    void shouldFilterByNamePattern() {
        // Given
        Person maria = Person.create("1", "María García", 30);
        Person john = Person.create("2", "John Smith", 35);
        
        // When/Then
        assertTrue(filterBean.nameContains(maria, "mar")); // Case insensitive
        assertTrue(filterBean.nameContains(maria, "García"));
        assertTrue(filterBean.nameContains(john, "smith")); // Case insensitive
        assertFalse(filterBean.nameContains(john, "García"));
        assertFalse(filterBean.nameContains(maria, null));
    }
    
    @Test
    void shouldHandleExchangeFilter() {
        // Given
        var camelContext = new DefaultCamelContext();
        var exchange = new DefaultExchange(camelContext);
        
        Person validPerson = Person.create("123", "Valid Person", 30);
        exchange.getIn().setBody(validPerson);
        exchange.getIn().setHeader("CamelFileName", "valid_data.xml");
        
        // When/Then
        assertTrue(filterBean.isValidExchange(exchange));
        
        // Test con archivo de test (debería fallar)
        exchange.getIn().setHeader("CamelFileName", "test_data.xml");
        assertFalse(filterBean.isValidExchange(exchange));
    }
    
    @Test
    void shouldFilterEveryThirdPerson() {
        // Given
        Person person1 = Person.create("1", "Person 1", 20);
        Person person2 = Person.create("2", "Person 2", 25);
        Person person3 = Person.create("3", "Person 3", 30);
        Person person4 = Person.create("4", "Person 4", 35);
        
        // When/Then - Solo el 3er elemento debe pasar
        assertFalse(statefulFilter.everyThirdPerson(person1)); // 1º
        assertFalse(statefulFilter.everyThirdPerson(person2)); // 2º
        assertTrue(statefulFilter.everyThirdPerson(person3));  // 3º ✓
        assertFalse(statefulFilter.everyThirdPerson(person4)); // 4º
    }
    
    @Test
    void shouldMaintainAgeDistribution() {
        // Given
        Person young1 = Person.create("1", "Young 1", 25);
        Person young2 = Person.create("2", "Young 2", 30);
        Person old1 = Person.create("3", "Old 1", 50);
        Person old2 = Person.create("4", "Old 2", 60);
        
        // Reset para test limpio
        statefulFilter.resetCounters();
        
        // When/Then - Primeros 50 elementos favorecen jóvenes
        assertTrue(statefulFilter.maintainAgeDistribution(young1)); // < 40
        assertTrue(statefulFilter.maintainAgeDistribution(young2)); // < 40
        assertFalse(statefulFilter.maintainAgeDistribution(old1)); // >= 40
        assertFalse(statefulFilter.maintainAgeDistribution(old2)); // >= 40
    }
    
    @Test
    void shouldValidateComplexCriteria() {
        // Given
        Person valid = Person.create("123", "Valid Person", 30);
        Person nullPerson = null;
        Person invalidId = Person.create("", "Valid Name", 30);
        Person shortName = Person.create("123", "A", 30);
        Person invalidAge = Person.create("123", "Valid Name", -5);
        
        // When/Then
        assertTrue(statefulFilter.complexValidation(valid));
        assertFalse(statefulFilter.complexValidation(nullPerson));
        assertFalse(statefulFilter.complexValidation(invalidId));
        assertFalse(statefulFilter.complexValidation(shortName));
        assertFalse(statefulFilter.complexValidation(invalidAge));
    }
    
    @Test
    void shouldProvideStats() {
        // Given
        statefulFilter.resetCounters();
        
        // When
        String initialStats = statefulFilter.getStats();
        
        // Process some data to update stats
        statefulFilter.isAboveAverageAge(Person.create("1", "Person 1", 30));
        statefulFilter.isAboveAverageAge(Person.create("2", "Person 2", 40));
        
        String updatedStats = statefulFilter.getStats();
        
        // Then
        assertEquals("Processed: 0, Adults: 0, Avg Age: 0.0", initialStats);
        assertTrue(updatedStats.contains("Processed: 2"));
        assertTrue(updatedStats.contains("Adults: 2"));
        assertTrue(updatedStats.contains("Avg Age: 35.0"));
    }
}