package com.curso.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.time.Duration;

@Component
@Qualifier("ProcesadorRuta2")
public class ProcesadorBaconIpsum implements Processor {

    private final String urlBaconIpsum;
    private final int timeoutConexion;
    private final int timeoutLectura;
    private final boolean proxyEnabled;
    private final String proxyHost;
    private final int proxyPort;

    public ProcesadorBaconIpsum(
            @Value("${bacon.ipsum.url}") String urlBaconIpsum,
            @Value("${bacon.ipsum.timeout.conexion:5}") int timeoutConexion,
            @Value("${bacon.ipsum.timeout.lectura:10}") int timeoutLectura,
            @Value("${bacon.ipsum.proxy.enabled:false}") boolean proxyEnabled,
            @Value("${bacon.ipsum.proxy.host:}") String proxyHost,
            @Value("${bacon.ipsum.proxy.port:0}") int proxyPort) {
        this.urlBaconIpsum = urlBaconIpsum;
        this.timeoutConexion = timeoutConexion;
        this.timeoutLectura = timeoutLectura;
        this.proxyEnabled = proxyEnabled;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        
        System.out.println("Creando ProcesadorBaconIpsum con URL: " + urlBaconIpsum);
        if (proxyEnabled) {
            System.out.println("Proxy configurado: " + proxyHost + ":" + proxyPort);
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("Procesando mensaje en ProcesadorBaconIpsum...");
        
        try {
            // Hacer petición HTTP a BaconIpsum API
            String baconText = obtenerBaconIpsum();
            
            // Obtener el mensaje entrante y modificar su body
            Message mensajeEntrante = exchange.getIn();
            mensajeEntrante.setBody(baconText);
            
            // Añadir un header indicando el origen del contenido
            mensajeEntrante.setHeader("Origen-Contenido", "BaconIpsum-API");
            mensajeEntrante.setHeader("Timestamp-Procesamiento", System.currentTimeMillis());
            
            System.out.println("Contenido BaconIpsum obtenido y asignado al body del mensaje");
            
        } catch (Exception e) {
            System.err.println("Error al obtener contenido de BaconIpsum: " + e.getMessage());
            // En caso de error, asignar un mensaje por defecto
            Message mensajeEntrante = exchange.getIn();
            mensajeEntrante.setBody("Error al obtener BaconIpsum: " + e.getMessage());
            mensajeEntrante.setHeader("Origen-Contenido", "Error-Fallback");
        }
    }

    private String obtenerBaconIpsum() throws Exception {
        // Crear el cliente HTTP moderno con configuración desde propiedades
        HttpClient.Builder clienteBuilder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutConexion));
        
        // Configurar proxy si está habilitado
        if (proxyEnabled && proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            ProxySelector proxySelector = ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort));
            clienteBuilder.proxy(proxySelector);
            System.out.println("Usando proxy: " + proxyHost + ":" + proxyPort);
        }
        
        HttpClient cliente = clienteBuilder.build();
        
        // Construir la petición usando el patrón builder con URL configurable
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(urlBaconIpsum))
                .timeout(Duration.ofSeconds(timeoutLectura))
                .header("User-Agent", "CamelFormacion/1.0")
                .GET()
                .build();
        
        // Enviar la petición y obtener la respuesta
        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        
        // Verificar código de respuesta
        if (respuesta.statusCode() != 200) {
            throw new Exception("Error HTTP: " + respuesta.statusCode());
        }
        
        return respuesta.body().trim();
    }
}