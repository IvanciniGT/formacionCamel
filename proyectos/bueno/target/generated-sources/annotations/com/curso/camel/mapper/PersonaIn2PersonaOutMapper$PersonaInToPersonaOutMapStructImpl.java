package com.curso.camel.mapper;

import com.curso.camel.model.PersonaIn;
import com.curso.camel.model.PersonaOutImpl;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-09T13:59:59+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
class PersonaIn2PersonaOutMapper$PersonaInToPersonaOutMapStructImpl implements PersonaIn2PersonaOutMapper.PersonaInToPersonaOutMapStruct {

    @Override
    public PersonaOutImpl personaInToPersonaOut(PersonaIn personaIn) {
        if ( personaIn == null ) {
            return null;
        }

        PersonaOutImpl personaOutImpl = new PersonaOutImpl();

        personaOutImpl.setId( personaIn.getId() );
        personaOutImpl.setDNI( personaIn.getDNI() );
        personaOutImpl.setNombre( personaIn.getNombre() );
        personaOutImpl.setDireccion( mapDireccion( personaIn ) );
        personaOutImpl.setDatosContacto( mapDatosContacto( personaIn ) );

        return personaOutImpl;
    }
}
