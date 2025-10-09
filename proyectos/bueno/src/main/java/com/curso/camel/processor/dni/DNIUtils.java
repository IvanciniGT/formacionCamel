package com.curso.camel.processor.dni;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DNIUtils  {

    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final String PATRON_DNI = "^((\\d{1,8})|(\\d{1,3}[.]\\d{3})|(\\d{1,2}[.]\\d{3}[.]\\d{3}))([ -]?)([A-Za-z])$"; 
    private static final Pattern pattern = Pattern.compile(PATRON_DNI);
    
    public static boolean esDNIValido(String dni) {
        // Validar si cumple el patrón
        Matcher matcher = pattern.matcher(dni);
        if (!matcher.matches()) {
            return false;
        }
        // Quitamos puntos si los trae
        String dniSinPuntos = dni.replaceAll("[. -]", "");
        // Obtener la parte numérica y la letra
        String numeroStr = dniSinPuntos.toUpperCase().substring(0, dniSinPuntos.length() - 1);
        char letra = Character.toUpperCase(dniSinPuntos.charAt(dniSinPuntos.length() - 1));
        // Validar que la parte numérica es
        int numero = Integer.parseInt(numeroStr);
        // Calcular la letra correcta
        return LETRAS_DNI.charAt(numero % 23) == letra;
    }

}
