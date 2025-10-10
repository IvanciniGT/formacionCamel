package com.example.camel.service;

import com.example.camel.model.Person;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonStatus;
import com.example.camel.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de personas.
 * Encapsula la lógica de acceso a datos y reglas de negocio.
 * 
 * Este servicio puede ser usado desde:
 * - Rutas Camel (usando .bean(personService, "methodName"))
 * - Controladores REST
 * - Otros servicios
 */
@Service
@Transactional
public class PersonService {
    
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);
    
    private final PersonRepository personRepository;
    
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    
    /**
     * Guardar o actualizar una persona
     * Si existe por externalId, actualiza; si no, crea nueva
     * @param person datos de la persona (interfaz Person)
     * @return entidad guardada
     */
    public PersonEntity saveOrUpdate(Person person) {
        log.debug("Guardando persona: {}", person);
        
        PersonEntity entity;
        
        // Buscar si ya existe por external ID
        if (person.getId() != null) {
            entity = personRepository.findByExternalId(person.getId())
                    .orElse(new PersonEntity());
            entity.setExternalId(person.getId());
        } else {
            entity = new PersonEntity();
        }
        
        // Actualizar datos
        entity.setName(person.getName());
        entity.setAge(person.getAge());
        entity.setStatus(determineStatus(person));
        
        PersonEntity saved = personRepository.save(entity);
        log.info("Persona guardada con ID: {}", saved.getId());
        
        return saved;
    }
    
    /**
     * Buscar todas las personas
     * @return lista de todas las personas
     */
    @Transactional(readOnly = true)
    public List<PersonEntity> findAll() {
        log.debug("Buscando todas las personas");
        return personRepository.findAll();
    }
    
    /**
     * Buscar solo personas activas
     * @return lista de personas activas
     */
    @Transactional(readOnly = true)
    public List<PersonEntity> findAllActive() {
        log.debug("Buscando personas activas");
        return personRepository.findAllActive();
    }
    
    /**
     * Buscar persona por ID de base de datos
     * @param id ID de la base de datos
     * @return Optional con la persona si existe
     */
    @Transactional(readOnly = true)
    public Optional<PersonEntity> findById(Long id) {
        log.debug("Buscando persona por ID: {}", id);
        return personRepository.findById(id);
    }
    
    /**
     * Buscar persona por ID externo (del XML/JSON original)
     * @param externalId ID externo
     * @return Optional con la persona si existe
     */
    @Transactional(readOnly = true)
    public Optional<PersonEntity> findByExternalId(String externalId) {
        log.debug("Buscando persona por external ID: {}", externalId);
        return personRepository.findByExternalId(externalId);
    }
    
    /**
     * Buscar personas por rango de edad
     * @param minAge edad mínima
     * @param maxAge edad máxima
     * @return lista de personas en el rango
     */
    @Transactional(readOnly = true)
    public List<PersonEntity> findByAgeRange(Integer minAge, Integer maxAge) {
        log.debug("Buscando personas entre {} y {} años", minAge, maxAge);
        return personRepository.findByAgeBetween(minAge, maxAge);
    }
    
    /**
     * Buscar adultos activos (>= 18 años y estado ACTIVE)
     * @return lista de adultos activos
     */
    @Transactional(readOnly = true)
    public List<PersonEntity> findAdults() {
        log.debug("Buscando adultos activos");
        return personRepository.findAdultsByStatus(18, PersonStatus.ACTIVE);
    }
    
    /**
     * Buscar personas por nombre (búsqueda parcial)
     * @param name parte del nombre a buscar
     * @return lista de personas con nombres coincidentes
     */
    @Transactional(readOnly = true)
    public List<PersonEntity> searchByName(String name) {
        log.debug("Buscando personas por nombre: {}", name);
        return personRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Contar personas por estado
     * @param status estado a contar
     * @return número de personas con ese estado
     */
    @Transactional(readOnly = true)
    public long countByStatus(PersonStatus status) {
        return personRepository.countByStatus(status);
    }
    
    /**
     * Eliminar persona (soft delete - cambiar estado a INACTIVE)
     * @param id ID de la persona
     */
    public void deactivate(Long id) {
        log.info("Desactivando persona con ID: {}", id);
        personRepository.findById(id).ifPresent(person -> {
            person.setStatus(PersonStatus.INACTIVE);
            personRepository.save(person);
        });
    }
    
    /**
     * Eliminar físicamente persona de la base de datos
     * @param id ID de la persona
     */
    public void delete(Long id) {
        log.warn("Eliminando físicamente persona con ID: {}", id);
        personRepository.deleteById(id);
    }
    
    /**
     * Procesar y guardar una persona desde Camel
     * Método útil para usar desde rutas Camel con .bean()
     * @param person datos de la persona
     * @return JSON de la entidad guardada (para logging/respuesta)
     */
    public String processAndSave(Person person) {
        log.info("Procesando y guardando persona desde Camel: {}", person);
        PersonEntity saved = saveOrUpdate(person);
        return String.format("{\"id\":%d,\"externalId\":\"%s\",\"name\":\"%s\",\"status\":\"%s\"}", 
                           saved.getId(), 
                           saved.getExternalId(), 
                           saved.getName(), 
                           saved.getStatus());
    }
    
    /**
     * Obtener estadísticas básicas
     * @return estadísticas en formato texto
     */
    @Transactional(readOnly = true)
    public String getStatistics() {
        long total = personRepository.count();
        long active = personRepository.countByStatus(PersonStatus.ACTIVE);
        long inactive = personRepository.countByStatus(PersonStatus.INACTIVE);
        Double avgAge = personRepository.getAverageAge(PersonStatus.ACTIVE);
        
        return String.format(
            "📊 Estadísticas: Total=%d, Activos=%d, Inactivos=%d, Edad Promedio=%.1f",
            total, active, inactive, avgAge != null ? avgAge : 0.0
        );
    }
    
    // =================== MÉTODOS PRIVADOS DE UTILIDAD ===================
    
    /**
     * Determinar estado de la persona basado en sus datos
     */
    private PersonStatus determineStatus(Person person) {
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            return PersonStatus.INCOMPLETE;
        }
        if (person.getAge() == null || person.getAge() < 0 || person.getAge() > 150) {
            return PersonStatus.INVALID;
        }
        return PersonStatus.ACTIVE;
    }
}