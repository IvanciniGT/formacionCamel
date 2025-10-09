package com.curso.camel.processor.email;

import org.apache.camel.Processor;

public interface EmailProcessor extends Processor {

    String EMAIL_PROCESSOR_EXCHANGE_PROPERTY_NAME = "EmailValido";

}
