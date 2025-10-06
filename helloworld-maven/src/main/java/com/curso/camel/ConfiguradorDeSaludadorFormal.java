package com.curso.camel;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
@Configuration // Le indica a Spring que esta clase contiene configuraciones y componentes
public class ConfiguradorDeSaludadorFormal {

    public ConfiguradorDeSaludadorFormal() {
        System.out.println("Creando una instancia de ConfiguradorDeSaludadorFormal");
    }

    // Este método es el que crea la instancia del SaludadorFormal
    @Bean
    public Saludador federico() {
        return new SaludadorFormal();
    }
}