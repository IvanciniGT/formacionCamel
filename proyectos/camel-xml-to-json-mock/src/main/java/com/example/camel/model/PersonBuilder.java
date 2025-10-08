package com.example.camel.model;

/**
 * Builder pattern para crear instancias de Person de manera fluida.
 * Permite construir objetos Person paso a paso con una API clara.
 * 
 * Ejemplo de uso:
 * <pre>
 * Person person = PersonBuilder.builder()
 *     .id("123")
 *     .name("Ada Lovelace")
 *     .age(36)
 *     .build();
 * </pre>
 */
public class PersonBuilder {
    
    private String id;
    private String name;
    private Integer age;
    
    /**
     * Constructor privado para forzar el uso del método estático builder()
     */
    private PersonBuilder() {
    }
    
    /**
     * Punto de entrada para crear un nuevo builder
     * @return nueva instancia del builder
     */
    public static PersonBuilder builder() {
        return new PersonBuilder();
    }
    
    /**
     * Establece el ID de la persona
     * @param id identificador único
     * @return this builder para encadenamiento
     */
    public PersonBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    /**
     * Establece el nombre de la persona
     * @param name nombre completo
     * @return this builder para encadenamiento
     */
    public PersonBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Establece la edad de la persona
     * @param age edad en años
     * @return this builder para encadenamiento
     */
    public PersonBuilder age(Integer age) {
        this.age = age;
        return this;
    }
    
    /**
     * Construye y devuelve la instancia final de Person
     * @return nueva instancia de Person con los valores configurados
     */
    public Person build() {
        return new PersonImpl(id, name, age);
    }
    
    /**
     * Crea una copia del builder a partir de una instancia existente de Person
     * @param person instancia existente para copiar
     * @return builder con los valores copiados
     */
    public static PersonBuilder from(Person person) {
        return builder()
            .id(person.getId())
            .name(person.getName())
            .age(person.getAge());
    }
}