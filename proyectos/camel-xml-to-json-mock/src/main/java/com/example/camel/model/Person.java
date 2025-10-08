package com.example.camel.model;

/**
 * Interfaz que define el contrato para una persona.
 * Permite diferentes implementaciones según el contexto (DTO, Entity, etc.)
 */
public interface Person {
    
    /**
     * Obtiene el identificador único de la persona
     * @return el ID de la persona
     */
    String getId();
    
    /**
     * Establece el identificador único de la persona
     * @param id el ID a asignar
     */
    void setId(String id);
    
    /**
     * Obtiene el nombre de la persona
     * @return el nombre de la persona
     */
    String getName();
    
    /**
     * Establece el nombre de la persona
     * @param name el nombre a asignar
     */
    void setName(String name);
    
    /**
     * Obtiene la edad de la persona
     * @return la edad de la persona
     */
    Integer getAge();
    
    /**
     * Establece la edad de la persona
     * @param age la edad a asignar
     */
    void setAge(Integer age);
    
    /**
     * Crea una nueva instancia de PersonImpl
     * @return nueva instancia implementando Person
     */
    static Person create() {
        return new PersonImpl();
    }
    
    /**
     * Crea una nueva instancia con los datos proporcionados
     * @param id identificador
     * @param name nombre
     * @param age edad
     * @return nueva instancia con los datos asignados
     */
    static Person create(String id, String name, Integer age) {
        PersonImpl person = new PersonImpl();
        person.setId(id);
        person.setName(name);
        person.setAge(age);
        return person;
    }
    
    /**
     * Crea un builder para construir instancias Person de manera fluida
     * @return nuevo builder
     */
    static PersonBuilder builder() {
        return PersonBuilder.builder();
    }
}
