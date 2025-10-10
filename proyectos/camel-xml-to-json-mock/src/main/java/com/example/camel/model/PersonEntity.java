package com.example.camel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para persistencia de personas en base de datos.
 * Incluye metadatos de auditoría (fechas de creación/actualización).
 * 
 * Mapea a la tabla 'persons' en la base de datos.
 */
@Entity
@Table(name = "persons", indexes = {
    @Index(name = "idx_person_name", columnList = "name"),
    @Index(name = "idx_person_age", columnList = "age"),
    @Index(name = "idx_person_status", columnList = "status")
})
public class PersonEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "external_id", unique = true, length = 100)
    private String externalId; // El ID que venía del XML/JSON original
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private PersonStatus status;
    
    /**
     * Constructor por defecto (requerido por JPA)
     */
    public PersonEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PersonStatus.ACTIVE;
    }
    
    /**
     * Constructor con parámetros básicos
     */
    public PersonEntity(String externalId, String name, Integer age) {
        this();
        this.externalId = externalId;
        this.name = name;
        this.age = age;
    }
    
    /**
     * Callback ejecutado antes de persistir (INSERT)
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Callback ejecutado antes de actualizar (UPDATE)
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // ====================== GETTERS Y SETTERS ======================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public PersonStatus getStatus() {
        return status;
    }
    
    public void setStatus(PersonStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "PersonEntity{" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", status=" + status +
                '}';
    }
}