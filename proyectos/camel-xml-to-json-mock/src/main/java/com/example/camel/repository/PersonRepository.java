package com.example.camel.repository;

import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para PersonEntity.
 * Spring Data genera automáticamente la implementación.
 * 
 * Métodos disponibles automáticamente:
 * - findAll(), findById(), save(), delete(), etc.
 * 
 * Se pueden agregar queries personalizadas usando:
 * - Naming conventions (findByXXX)
 * - @Query con JPQL
 * - @Query con SQL nativo
 */
@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    
    // =============== QUERY METHODS (Spring genera la implementación) ===============
    
    /**
     * Buscar persona por ID externo (el ID que venía del XML/JSON)
     * @param externalId el ID externo
     * @return Optional con la persona si existe
     */
    Optional<PersonEntity> findByExternalId(String externalId);
    
    /**
     * Buscar personas por nombre (búsqueda exacta)
     * @param name nombre a buscar
     * @return lista de personas con ese nombre
     */
    List<PersonEntity> findByName(String name);
    
    /**
     * Buscar personas por nombre (búsqueda parcial, case insensitive)
     * @param name parte del nombre a buscar
     * @return lista de personas con nombres que contengan el texto
     */
    List<PersonEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * Buscar personas por edad
     * @param age edad a buscar
     * @return lista de personas con esa edad
     */
    List<PersonEntity> findByAge(Integer age);
    
    /**
     * Buscar personas en un rango de edad
     * @param minAge edad mínima (inclusive)
     * @param maxAge edad máxima (inclusive)
     * @return lista de personas en el rango
     */
    List<PersonEntity> findByAgeBetween(Integer minAge, Integer maxAge);
    
    /**
     * Buscar personas mayores o iguales a una edad
     * @param age edad mínima
     * @return lista de personas con edad >= age
     */
    List<PersonEntity> findByAgeGreaterThanEqual(Integer age);
    
    /**
     * Buscar personas por estado
     * @param status estado a buscar
     * @return lista de personas con ese estado
     */
    List<PersonEntity> findByStatus(PersonStatus status);
    
    /**
     * Buscar personas activas
     * @return lista de personas con estado ACTIVE
     */
    default List<PersonEntity> findAllActive() {
        return findByStatus(PersonStatus.ACTIVE);
    }
    
    /**
     * Contar personas por estado
     * @param status estado a contar
     * @return número de personas con ese estado
     */
    long countByStatus(PersonStatus status);
    
    /**
     * Verificar si existe una persona con un external ID
     * @param externalId el ID externo
     * @return true si existe
     */
    boolean existsByExternalId(String externalId);
    
    // =============== CUSTOM QUERIES CON @Query ===============
    
    /**
     * Buscar personas con query JPQL personalizada
     * @param minAge edad mínima
     * @param status estado
     * @return lista de personas que cumplen los criterios
     */
    @Query("SELECT p FROM PersonEntity p WHERE p.age >= :minAge AND p.status = :status ORDER BY p.name")
    List<PersonEntity> findAdultsByStatus(@Param("minAge") Integer minAge, 
                                         @Param("status") PersonStatus status);
    
    /**
     * Buscar personas VIP (ejemplo: nombre con múltiples palabras)
     * Query SQL nativa
     */
    @Query(value = "SELECT * FROM persons WHERE LENGTH(name) - LENGTH(REPLACE(name, ' ', '')) >= 2", 
           nativeQuery = true)
    List<PersonEntity> findVipPersons();
    
    /**
     * Obtener estadísticas de edad
     */
    @Query("SELECT AVG(p.age) FROM PersonEntity p WHERE p.status = :status")
    Double getAverageAge(@Param("status") PersonStatus status);
    
    /**
     * Buscar personas creadas recientemente (últimos N días)
     */
    @Query("SELECT p FROM PersonEntity p WHERE p.createdAt >= CURRENT_TIMESTAMP - :days DAY ORDER BY p.createdAt DESC")
    List<PersonEntity> findRecentlyCreated(@Param("days") int days);
    
    /**
     * Buscar por múltiples criterios con query dinámica
     */
    @Query("SELECT p FROM PersonEntity p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minAge IS NULL OR p.age >= :minAge) AND " +
           "(:maxAge IS NULL OR p.age <= :maxAge) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<PersonEntity> searchPersons(@Param("name") String name,
                                     @Param("minAge") Integer minAge,
                                     @Param("maxAge") Integer maxAge,
                                     @Param("status") PersonStatus status);
}