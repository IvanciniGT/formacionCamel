# 🔄 XML to JSON Processor

> **Proyecto educativo** que demuestra el procesamiento de archivos XML → JSON usando Apache Camel 4.x y Spring Boot 3.x

## 📋 ¿Qué hace?

Este procesador transforma automáticamente archivos XML de personas a formato JSON:

- 📁 **Input**: Archivos XML en carpeta `input/`
- 🔧 **Process**: Parseo, limpieza y validación de datos
- 💾 **Output**: Archivos JSON en carpeta `output/`
- ✅ **Cleanup**: Archivos originales movidos a `done/`

## 🏗️ Arquitectura

```
input/ ──► XmlToJsonTransformRoute ──► PersonDataProcessor ──► output/
  │              │                           │                    │
  XML         (Apache Camel)            (Data cleaning)         JSON
```

## 🚀 Cómo usar

### 1. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 2. Procesar archivos
```bash
# Copia algunos ejemplos a la carpeta input
cp docs/examples/*.xml input/

# Los archivos se procesarán automáticamente
# Revisa la carpeta output/ para ver los JSON generados
```

## 🧪 Testing

El proyecto incluye varios tipos de tests:

### Tests Unitarios
```bash
# Solo el procesador (sin Spring/Camel)
mvn test -Dtest=PersonDataProcessorTest
```

### Tests de Integración
```bash
# Ruta completa con mocks
mvn test -Dtest=XmlToJsonTransformRouteTest
```

### Todos los tests
```bash
mvn test
```

## 📚 Documentación

- 📖 **[Arquitectura](docs/architecture.md)**: Diseño detallado del sistema
- 💡 **[Ejemplos](docs/examples/)**: Archivos XML/JSON de muestra

## 🛠️ Tecnologías

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.6** - Framework de aplicación
- **Apache Camel 4.10.7** - Framework de integración
- **Jackson XML/JSON** - Serialización/deserialización
- **JUnit 5** - Testing

## ⚙️ Configuración

La configuración se centraliza en `application.yml`:

```yaml
app:
  input:
    path: "file:input?move=done&readLock=markerFile"
  output:
    path: "file:output?fileName=${file:name.noext}.json"
```

## 📁 Estructura del Proyecto

```
├── input/              # 📁 Archivos XML a procesar
├── output/             # 📁 Archivos JSON generados  
├── done/               # 📁 Archivos XML procesados
├── docs/               # 📚 Documentación
├── src/main/java/
│   └── com/example/camel/
│       ├── model/      # 🏗️ Modelos de datos
│       ├── processor/  # ⚙️ Lógica de procesamiento
│       └── routes/     # 🔄 Rutas Camel
└── src/test/           # 🧪 Tests unitarios e integración
```

## 🎯 Conceptos Demostrados

- **File Endpoints**: Monitoreo de carpetas con Camel
- **Data Format**: Transformación XML ↔ JSON con Jackson
- **Processors**: Lógica de negocio personalizada
- **Route Testing**: Mocking con `@UseAdviceWith`
- **Configuration**: Externalización con Spring Boot
- **Logging**: Trazabilidad del procesamiento

## 🚀 Para producción

Para usar en entornos reales, considera:

- 🔒 **Seguridad**: Validación de esquemas XML/JSON
- 📊 **Monitoreo**: Métricas y alertas
- 🔄 **Resilencia**: Manejo de errores y reintentos
- 🏗️ **Escalabilidad**: Procesamiento paralelo
- 📝 **Auditoría**: Logs estructurados y trazabilidad

---

**¡Happy Coding!** 🎉
```

Si quieres ejecutar la aplicación (usará `file:input` y `file:output`):

```bash
mkdir -p input output
mvn -q spring-boot:run
```

Coloca un `*.xml` de `Person` en `input/` y observarás el `*.json` en `output/`.
