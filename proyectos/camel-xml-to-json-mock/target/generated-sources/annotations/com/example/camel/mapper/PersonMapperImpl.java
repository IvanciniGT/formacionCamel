package com.example.camel.mapper;

import com.example.camel.model.Person;
import com.example.camel.model.PersonBuilder;
import com.example.camel.model.PersonEntity;
import com.example.camel.model.PersonSummary;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-08T15:49:51+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public PersonEntity personToEntity(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonEntity personEntity = new PersonEntity();

        personEntity.setId( person.getId() );
        personEntity.setName( person.getName() );
        personEntity.setAge( person.getAge() );

        return personEntity;
    }

    @Override
    public Person entityToPerson(PersonEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PersonBuilder person = Person.builder();

        person.age( entity.getAge() );
        person.id( entity.getId() );
        person.name( entity.getName() );

        return person.build();
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

        personEntity.setName( formatName( person.getName() ) );
        personEntity.setId( person.getId() );
        personEntity.setAge( person.getAge() );

        return personEntity;
    }
}
