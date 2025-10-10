package com.curso.camel.integration;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.curso.camel.filter.SoloDNIsValidosImpl;
import com.curso.camel.processor.dni.DNIProcessorImpl;

// Solo se activa con el perfil "test-integration"
@Component
@Profile("test-integration")
public class RutaParaPruebas extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct::start")
            .process( new DNIProcessorImpl() )
            .filter( new SoloDNIsValidosImpl()::matches )
            .to("mock:result");
    }
}
