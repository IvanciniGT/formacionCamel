package com.curso.camel.mapper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.curso.camel.model.PersonaIn;
import com.curso.camel.model.PersonaOutImpl;
import com.curso.camel.processor.edad.EdadProcessor;

@Component
public class PersonaIn2PersonaOutMapper implements Processor {

    // Instancia del mapper de MapStruct
    private final PersonaInToPersonaOutMapStruct mapStructMapper = Mappers.getMapper(PersonaInToPersonaOutMapStruct.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        // 1. Obtener el PersonaIn del body del exchange
        PersonaIn personaIn = exchange.getIn().getBody(PersonaIn.class);
        
        // 2. Usar MapStruct para hacer el mapeo automático
        PersonaOutImpl personaOut = mapStructMapper.personaInToPersonaOut(personaIn);
        
        // 3. Obtener la edad del Exchange (calculada previamente por EdadProcessor)
        Integer edad = (Integer) exchange.getProperty(EdadProcessor.EDAD_PROCESSOR_EXCHANGE_PROPERTY_NAME);
        personaOut.setEdad(edad != null ? edad : 0);
        
        // 4. Establecer el PersonaOut en el body del exchange
        exchange.getIn().setBody(personaOut);
    }

    // Interfaz interna de MapStruct para hacer el mapeo automático
    @Mapper(componentModel = "spring")
    interface PersonaInToPersonaOutMapStruct {
        
        @Mapping(target = "edad", ignore = true) // Lo estableceremos manualmente
        @Mapping(target = "direccion", source = "personaIn")
        @Mapping(target = "datosContacto", source = "personaIn")
        PersonaOutImpl personaInToPersonaOut(PersonaIn personaIn);
        
        // Mapper para Direccion
        default PersonaOutImpl.DireccionImpl mapDireccion(PersonaIn personaIn) {
            if (personaIn == null) {
                return null;
            }
            return new PersonaOutImpl.DireccionImpl(
                personaIn.getDireccion(),  // calle
                personaIn.getPoblacion(),   // ciudad
                personaIn.getCp(),          // codigoPostal
                personaIn.getPais()         // pais
            );
        }
        
        // Mapper para DatosContacto
        default PersonaOutImpl.DatosContactoImpl mapDatosContacto(PersonaIn personaIn) {
            if (personaIn == null) {
                return null;
            }
            return new PersonaOutImpl.DatosContactoImpl(
                java.util.List.of(personaIn.getTelefono()),  // telefonos
                java.util.List.of(personaIn.getEmail())      // emails
            );
        }
    }
}
