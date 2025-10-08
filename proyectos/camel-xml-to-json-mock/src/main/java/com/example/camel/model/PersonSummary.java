package com.example.camel.model;

/**
 * DTO simplificado para resumen de persona.
 * Usado para transformaciones y API responses ligeras.
 */
public class PersonSummary {
    
    private String identifier;
    private String fullName;
    private String ageGroup;
    
    public PersonSummary() {
    }
    
    public PersonSummary(String identifier, String fullName, String ageGroup) {
        this.identifier = identifier;
        this.fullName = fullName;
        this.ageGroup = ageGroup;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getAgeGroup() {
        return ageGroup;
    }
    
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    
    @Override
    public String toString() {
        return "PersonSummary{" +
                "identifier='" + identifier + '\'' +
                ", fullName='" + fullName + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                '}';
    }
}