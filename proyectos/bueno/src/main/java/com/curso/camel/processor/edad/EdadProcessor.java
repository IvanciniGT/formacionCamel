package com.curso.camel.processor.edad;

import org.apache.camel.Processor;

public interface EdadProcessor extends Processor {

    String EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME = "Edad";

}
