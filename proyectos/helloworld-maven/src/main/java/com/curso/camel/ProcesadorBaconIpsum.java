package com.curso.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@Qualifier("ProcesadorRuta2")
public class ProcesadorBaconIpsum implements Processor {

    public ProcesadorBaconIpsum() {
        System.out.println("Creando una instancia de ProcesadorBaconIpsum");
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
        // Crear el cliente HTTP moderno con configuración
        HttpClient cliente = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        
        // Construir la petición usando el patrón builder
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create("https://baconipsum.com/api/?type=all-meat&paras=3&format=text"))
                .timeout(Duration.ofSeconds(10))
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