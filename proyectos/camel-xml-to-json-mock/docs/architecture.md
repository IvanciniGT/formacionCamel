# Arquitectura del Procesador XML → JSON

## 📋 Descripción General

Este proyecto implementa un procesador de archivos que transforma documentos XML de personas a formato JSON usando Apache Camel y Spring Boot.

## 🏗️ Arquitectura

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐    ┌─────────────┐
│   input/    │───▶│ XmlToJson    │───▶│ PersonData      │───▶│   output/   │
│ (XML files) │    │ TransformRoute│    │ Processor       │    │ (JSON files)│
└─────────────┘    └──────────────┘    └─────────────────┘    └─────────────┘
```

## 🔄 Flujo de Procesamiento

1. **Input**: Monitor de carpeta `input/` detecta nuevos archivos XML
2. **Parser**: Deserialización XML → Objeto `Person` (Jackson XML)
3. **Processor**: Limpieza y normalización de datos
4. **Serializer**: Objeto `Person` → JSON (Jackson JSON)
5. **Output**: Archivo JSON guardado en `output/`
6. **Cleanup**: Archivo original movido a `done/`

## 📁 Estructura de Directorios

```
input/          # Archivos XML a procesar
├── person1.xml
└── person2.xml

output/         # Archivos JSON generados
├── person1.json
└── person2.json

done/           # Archivos XML ya procesados
├── person1.xml
└── person2.xml
```

## ⚙️ Componentes Principales

### XmlToJsonTransformRoute
- **Responsabilidad**: Orquestación del flujo completo
- **Tecnología**: Apache Camel RouteBuilder
- **Configuración**: Endpoints configurables vía `application.yml`

### PersonDataProcessor
- **Responsabilidad**: Limpieza y validación de datos
- **Funciones**: 
  - Trim de espacios en blanco
  - Validaciones básicas
  - Normalización de datos

### Person (Modelo)
- **Responsabilidad**: Representación de datos
- **Atributos**: id, name, age
- **Anotaciones**: Jackson XML para parsing

## 🔧 Configuración

La configuración se centraliza en `application.yml`:

```yaml
app:
  input:
    path: "file:input?move=done&readLock=markerFile"
  output:
    path: "file:output?fileName=${file:name.noext}.json"
```

## 🧪 Testing

### Tests Unitarios
- `PersonDataProcessorTest`: Lógica de procesamiento aislada

### Tests de Integración
- `XmlToJsonTransformRouteTest`: Flujo completo con mocks

### Estrategia de Testing
- **@UseAdviceWith**: Mockeo de endpoints file:
- **MockEndpoint**: Verificación de resultados
- **Test Data**: Archivos de ejemplo organizados