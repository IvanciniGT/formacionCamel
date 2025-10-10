package com.example.camel.model;

/**
 * Estados posibles de una persona en el sistema.
 * Usado por la entidad JPA PersonEntity.
 */
public enum PersonStatus {
    /**
     * Persona activa en el sistema
     */
    ACTIVE,
    
    /**
     * Persona inactiva (soft delete)
     */
    INACTIVE,
    
    /**
     * Persona con datos incompletos
     */
    INCOMPLETE,
    
    /**
     * Persona con datos inválidos
     */
    INVALID,
    
    /**
     * Persona pendiente de validación
     */
    PENDING
}
