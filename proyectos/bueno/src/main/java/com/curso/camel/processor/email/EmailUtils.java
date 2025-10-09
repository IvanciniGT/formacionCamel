package com.curso.camel.processor.email;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailUtils  {

    // Patrón más estricto para validar emails
    private static final String PATRON_EMAIL = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    private static final Pattern pattern = Pattern.compile(PATRON_EMAIL);
    
    private EmailUtils() {
        // Constructor privado para evitar instanciación
    }
    
    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailTrimmed = email.trim();
        // Validar longitud razonable
        if (emailTrimmed.length() > 254) { // RFC 5321
            return false;
        }
        // No debe tener espacios
        if (emailTrimmed.contains(" ")) {
            return false;
        }
        // Validar si cumple el patrón
        Matcher matcher = pattern.matcher(emailTrimmed);
        return matcher.matches();
    }

}
