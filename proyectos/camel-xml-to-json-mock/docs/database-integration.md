# 🗄️ Integración JPA con Apache Camel

## 📋 Descripción General

Esta documentación describe la integración completa de **Spring Data JPA** con **Apache Camel** para persistencia y lectura de datos desde base de datos.

---

## 🏗️ Arquitectura de Capas

```
┌─────────────────────────────────────────────┐
│         Camel Routes (DatabaseRoute)         │  ← Orquestación y flujo
├─────────────────────────────────────────────┤
│         Service Layer (PersonService)        │  ← Lógica de negocio
├─────────────────────────────────────────────┤
│    Repository Layer (PersonRepository)       │  ← Acceso a datos
├─────────────────────────────────────────────┤
│         JPA/Hibernate (ORM)                  │  ← Mapeo objeto-relacional
├─────────────────────────────────────────────┤
│         Database (H2 in-memory)              │  ← Persistencia
└─────────────────────────────────────────────┘
```

---

## 🛠️ Componentes

### 1️⃣ **PersonEntity** (Entidad JPA)

```java
@Entity
@Table(name = "person", 
       indexes = {
           @Index(name = "idx_name", columnList = "name"),
           @Index(name = "idx_age", columnList = "age"),
           @Index(name = "idx_status", columnList = "status")
       })
public class PersonEntity implements Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "external_id", unique = true, length = 50)
    private String externalId;
    
    // ... otros campos
}
```

**Características:**
- ✅ Auto-generación de ID con `IDENTITY`
- ✅ `externalId` único para integración con sistemas externos
- ✅ Índices en campos frecuentemente consultados
- ✅ Callbacks `@PrePersist` y `@PreUpdate` para auditoría

---

### 2️⃣ **PersonRepository** (Acceso a Datos)

```java
@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    
    // 1. Query por convención de nombres
    Optional<PersonEntity> findByExternalId(String externalId);
    List<PersonEntity> findByStatus(PersonStatus status);
    
    // 2. JPQL Query
    @Query("SELECT p FROM PersonEntity p WHERE p.age >= :minAge AND p.status = 'ACTIVE'")
    List<PersonEntity> findAdultsByStatus(@Param("minAge") Integer minAge);
    
    // 3. Native SQL Query
    @Query(value = "SELECT * FROM person WHERE age > 50 AND status = 'ACTIVE'", 
           nativeQuery = true)
    List<PersonEntity> findVipPersons();
}
```

**Tipos de consultas:**
1. **Method Naming**: `findBy`, `existsBy`, `countBy`
2. **@Query JPQL**: Queries independientes de BD
3. **Native SQL**: Queries específicas de la BD

---

### 3️⃣ **PersonService** (Lógica de Negocio)

```java
@Service
@Transactional
public class PersonService {
    
    @Autowired
    private PersonRepository personRepository;
    
    public PersonEntity saveOrUpdate(Person person) {
        // Buscar si existe por externalId
        Optional<PersonEntity> existing = 
            personRepository.findByExternalId(person.getExternalId());
        
        PersonEntity entity;
        if (existing.isPresent()) {
            // Actualizar existente
            entity = existing.get();
            entity.setName(person.getName());
            entity.setAge(person.getAge());
        } else {
            // Crear nuevo
            entity = PersonMapper.INSTANCE.personToEntity(person);
        }
        
        entity.setStatus(determineStatus(entity));
        return personRepository.save(entity);
    }
}
```

**Responsabilidades:**
- ✅ Validación y transformación de datos
- ✅ Lógica de negocio (soft delete, determinación de estado)
- ✅ Integración entre capas Camel y JPA
- ✅ Manejo transaccional con `@Transactional`

---

### 4️⃣ **DatabaseRoute** (Rutas Camel)

#### 🔄 **Ruta 1: JPA Consumer (Polling)**

```java
from("jpa:com.example.camel.model.PersonEntity?" +
     "consumeDelete=false&" +
     "consumer.delay=5000&" +
     "maximumResults=100")
    .log("Leyendo personas de BD")
    .split(body())
        .log("Persona: ${body}")
    .end();
```

**Opciones del JPA Consumer:**
- `consumeDelete=false`: No eliminar después de leer
- `consumer.delay=5000`: Polling cada 5 segundos
- `maximumResults=100`: Máximo 100 registros por poll

#### 📞 **Ruta 2: Usar Service Bean**

```java
from("timer:load-persons?period=10000")
    .bean(personService, "findAll")
    .split(body())
        .log("Persona: ${body.name}")
    .end();
```

**Ventajas:**
- ✅ Mayor control sobre la lógica
- ✅ Reutilización del service en otros contextos
- ✅ Transacciones gestionadas por Spring

#### 💾 **Ruta 3: Guardar desde Archivo XML**

```java
from("file:input-to-db?move=done")
    .unmarshal().jacksonXml(PersonImpl.class)
    .bean(personService, "saveOrUpdate")
    .log("Guardado con ID: ${body.id}")
    .marshal().json()
    .to("file:output-db");
```

**Flujo:**
1. Lee archivo XML
2. Parsea a objeto Java
3. Guarda en BD (upsert)
4. Convierte a JSON
5. Escribe resultado

---

## 🗃️ Base de Datos H2

### Configuración (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:persondb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

### 🌐 Acceso a H2 Console

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:persondb
User: sa
Password: (vacío)
```

### 📊 Datos Iniciales (`data.sql`)

```sql
-- 15 registros precargados
INSERT INTO person (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-001', 'Ada Lovelace', 36, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 10 ACTIVE, 2 INACTIVE, 1 PENDING, 1 INVALID, 1 INCOMPLETE
```

---

## 🚀 Rutas Disponibles

| Ruta | Trigger | Descripción |
|------|---------|-------------|
| `jpa-polling-route` | JPA Consumer (cada 5s) | Lee todas las personas automáticamente |
| `load-all-persons` | Timer (cada 10s) | Carga todas con service bean |
| `find-adults` | `direct:find-adults` | Busca adultos (≥18 años) |
| `find-by-age-range` | `direct:find-by-age-range` | Busca por rango de edad (headers) |
| `search-by-name` | `direct:search-by-name` | Busca por nombre (header) |
| `xml-to-database` | File `input-to-db/` | Guarda XML en BD |
| `export-database-to-json` | `direct:export-to-json` | Exporta BD completa a JSON |
| `show-statistics` | Timer (cada 30s) | Muestra estadísticas |
| `jpa-custom-query` | JPA Consumer (cada 15s) | Query JPQL personalizada |
| `deactivate-person` | `direct:deactivate-person` | Desactiva persona por ID |
| `enrich-database-from-files` | `direct:enrich-from-files` | Enriquece BD con archivos |

---

## 🧪 Pruebas

### 1. Copiar archivo XML a `input-to-db/`

```bash
cp docs/examples/katherine-johnson.xml input-to-db/
```

**Resultado esperado:**
- Archivo movido a `input-to-db/done/`
- JSON creado en `output-db/`
- Log: `✅ Persona guardada en BD con ID: X`

### 2. Invocar endpoint directo

```bash
# ProducerTemplate en código Java
camelContext.createProducerTemplate()
    .sendBody("direct:find-adults", null);
```

### 3. Verificar en H2 Console

```sql
SELECT * FROM person ORDER BY created_at DESC;
```

---

## 📈 Patrones y Mejores Prácticas

### ✅ **Usar Service Layer**
```java
// ❌ MAL: Llamar repository desde ruta
.bean(personRepository, "findAll")

// ✅ BIEN: Usar service layer
.bean(personService, "findAll")
```

### ✅ **Transacciones**
```java
@Transactional
public PersonEntity saveOrUpdate(Person person) {
    // Todas las operaciones en una sola transacción
}
```

### ✅ **Manejo de Errores**
```java
from("file:input-to-db")
    .onException(Exception.class)
        .handled(true)
        .log("Error: ${exception.message}")
        .to("file:errors")
    .end()
    .bean(personService, "saveOrUpdate");
```

### ✅ **Paginación en JPA Consumer**
```java
from("jpa:PersonEntity?maximumResults=50") // Solo 50 por poll
```

---

## 🔍 Queries Avanzadas

### Query Dinámica
```java
@Query("SELECT p FROM PersonEntity p WHERE " +
       "(:name IS NULL OR p.name LIKE %:name%) AND " +
       "(:minAge IS NULL OR p.age >= :minAge)")
List<PersonEntity> search(
    @Param("name") String name,
    @Param("minAge") Integer minAge
);
```

### Agregación
```java
@Query("SELECT COUNT(p) FROM PersonEntity p WHERE p.status = :status")
long countByStatus(@Param("status") PersonStatus status);
```

### Proyección
```java
@Query("SELECT new com.example.camel.model.PersonSummary(p.name, p.age) " +
       "FROM PersonEntity p WHERE p.status = 'ACTIVE'")
List<PersonSummary> findActiveSummaries();
```

---

## 🎯 Casos de Uso

1. **Importación masiva**: Leer archivos XML → Guardar en BD
2. **Exportación programada**: Timer → Exportar BD a JSON
3. **Sincronización**: JPA Consumer → Enviar a API externa
4. **Validación y filtrado**: Leer BD → Filtrar → Procesar
5. **Enriquecimiento**: Leer BD → Enriquecer con datos externos → Actualizar

---

## 📚 Referencias

- [Apache Camel JPA Component](https://camel.apache.org/components/latest/jpa-component.html)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [H2 Database Documentation](https://www.h2database.com/)
