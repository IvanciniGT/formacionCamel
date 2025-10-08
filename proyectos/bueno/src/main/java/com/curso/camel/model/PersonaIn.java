package com.curso.camel.model;

import java.time.LocalDate;

/* Los datos que me van a llegar para su procesamiento y despacho */
public interface PersonaIn {
    String getId();
    void setId(String id);
    
    String getDNI();
    void setDNI(String dni);
    
    String getNombre();
    void setNombre(String nombre);
    
    LocalDate getFechaDeNacimiento();
    void setFechaDeNacimiento(LocalDate fechaDeNacimiento);
    
    String getDireccion();
    void setDireccion(String direccion);
    
    String getPoblacion();
    void setPoblacion(String poblacion);
    
    String getCp();
    void setCp(String cp);
    
    String getPais();
    void setPais(String pais);
    
    String getTelefono();
    void setTelefono(String telefono);
    
    String getEmail();
    void setEmail(String email);
}