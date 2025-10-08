package com.curso.camel.processor.dni;

import org.apache.camel.Processor;

public interface DNIProcessor extends Processor {

    String DNI_PROCESSOR_EXCHANGE_PROPERTY_NAME = "DniValido";

}
