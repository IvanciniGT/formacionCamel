package com.curso.camel.processor.dni;

import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

// Un processor es una interfaz que debe implementar la función process(Exchange exchange)
@Component
public interface DNIProcessor extends Processor { }
