package com.example.camel.mapper;

import com.example.camel.model.Person;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonImpl;
import com.example.camel.model.PersonSummary;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-10T08:35:57+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public PersonEntity personToEntity(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonEntity personEntity = new PersonEntity();

        personEntity.setExternalId( person.getId() );
        personEntity.setName( person.getName() );
        personEntity.setAge( person.getAge() );

        return personEntity;
    }

    @Override
    public PersonImpl entityToPerson(PersonEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PersonImpl personImpl = new PersonImpl();

        personImpl.setId( entity.getExternalId() );
        personImpl.setName( entity.getName() );
        personImpl.setAge( entity.getAge() );

        return personImpl;
    }

    @Override
    public PersonSummary personToSummary(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonSummary personSummary = new PersonSummary();

        personSummary.setIdentifier( person.getId() );
        personSummary.setFullName( person.getName() );
        personSummary.setAgeGroup( ageToAgeGroup( person.getAge() ) );

        return personSummary;
    }

    @Override
    public PersonEntity personToEntityWithFormatting(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonEntity personEntity = new PersonEntity();

        personEntity.setExternalId( person.getId() );
        personEntity.setName( formatName( person.getName() ) );
        personEntity.setAge( person.getAge() );

        return personEntity;
    }
}
