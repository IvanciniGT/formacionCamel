# CONFIGURACIÓN DE PROXY PARA ENTORNOS CORPORATIVOS

## Opción 1: Configuración vía application.yaml
Modifica `src/main/resources/application.yaml`:

```yaml
bacon:
  ipsum:
    proxy:
      enabled: true
      host: "proxy.empresa.com"
      port: 8080
```

## Opción 2: Propiedades del sistema JVM (RECOMENDADO para proxies corporativos)
Ejecutar la aplicación con parámetros JVM:

```bash
# Para HTTP
mvn spring-boot:run -Dhttp.proxyHost=proxy.empresa.com -Dhttp.proxyPort=8080

# Para HTTPS
mvn spring-boot:run -Dhttps.proxyHost=proxy.empresa.com -Dhttps.proxyPort=8080

# Para ambos HTTP y HTTPS
mvn spring-boot:run \
  -Dhttp.proxyHost=proxy.empresa.com \
  -Dhttp.proxyPort=8080 \
  -Dhttps.proxyHost=proxy.empresa.com \
  -Dhttps.proxyPort=8080

# Con autenticación (si es necesaria)
mvn spring-boot:run \
  -Dhttp.proxyHost=proxy.empresa.com \
  -Dhttp.proxyPort=8080 \
  -Dhttp.proxyUser=usuario \
  -Dhttp.proxyPassword=password
```

## Opción 3: Variables de entorno
```bash
export http_proxy=http://proxy.empresa.com:8080
export https_proxy=http://proxy.empresa.com:8080
mvn spring-boot:run
```

## Opción 4: Configurar en IDE (IntelliJ/Eclipse)
Añadir a VM Options:
```
-Dhttp.proxyHost=proxy.empresa.com -Dhttp.proxyPort=8080 -Dhttps.proxyHost=proxy.empresa.com -Dhttps.proxyPort=8080
```

## Troubleshooting
Si sigues teniendo problemas:
1. Verifica la conectividad: `telnet proxy.empresa.com 8080`
2. Consulta con tu administrador de red el proxy correcto
3. Algunos proxies requieren autenticación adicional o certificados

## Para producción
Usa variables de entorno o archivos de configuración específicos por entorno:
- `application-dev.yaml`
- `application-prod.yaml`