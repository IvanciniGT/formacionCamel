package com.curso.camel.model;

import java.util.List;

/* Los datos que voy a despachar */
public interface PersonaOut {
    
    String getId();
    void setId(String id);
    
    String getDNI();
    void setDNI(String dni);
    
    String getNombre();
    void setNombre(String nombre);
    
    int getEdad();
    void setEdad(int edad);

    Direccion getDireccion();
    void setDireccion(Direccion direccion);

    DatosContacto getDatosContacto();
    void setDatosContacto(DatosContacto datosContacto);

    interface Direccion {
        String getCalle();
        void setCalle(String calle);
        
        String getCiudad();
        void setCiudad(String ciudad);
        
        String getCodigoPostal();
        void setCodigoPostal(String codigoPostal);
        
        String getPais();
        void setPais(String pais);
    }

    interface DatosContacto {
        List<String> getTelefonos();
        void setTelefonos(List<String> telefonos);
        
        List<String> getEmails();
        void setEmails(List<String> emails);
    }
}