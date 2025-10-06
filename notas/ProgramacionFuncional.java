import java.util.function.*;
// Dentro de este paquete tenemos interfaces funciones. 
// Cada interfaz funcional tiene un método que me permite 
// invocar a la función que representa.
// Es decir, interfaces para representar funciones:
//     Supplier<R>      No recibe argumentos y
//                      Devuelve un dato de tipo R 
//                        Los getters
//                      Método: R get()
//     Consumer<T>      Recibe un argumento de tipo T
//                      No devuelve nada
//                        Los setters
//                      Método: void accept(T t)
//     Predicate<T>     Recibe un argumento de tipo T
//                      Devuelve un boolean
//                        Los hasXXX, isXXX
//                      Método: boolean test(T t)
//     Function<T, R>.  Recibe un argumento de tipo T
//                      Devuelve un dato de tipo R
//                      Método: R apply(T t)
public class ProgramacionFuncional {

    private static void saluda(String nombre) {
        System.out.println("Hola " + nombre);
    }

    private static String generarSaludoFormal(String nombre) {
        return "Buenos días Sr./Sra. " + nombre;
    }

    private static String generarSaludoInformal(String nombre) {
        return "¿Qué pasa " + nombre + "?";
    }

    private static void imprimirSaludo(Function<String, String> funcion, String nombre) {
        System.out.println(funcion.apply(nombre));
    }

    public static void main(String[] args) {
        saluda("Menchu");
         
        Consumer<String> variable = ProgramacionFuncional::saluda; // En JAVA 1.8 aparece el operador :: que permite referenciar un método

        variable.accept("Federico"); // Aqui es donde ejecuto la función SALUDA... mediante la variable.

        imprimirSaludo(ProgramacionFuncional::generarSaludoFormal, "Menchu");
        imprimirSaludo(ProgramacionFuncional::generarSaludoInformal, "Menchu");
        // Cuando necesitamos crear una función, solo porque otra lo pide... y esa función que voy a crear no pienso reutilizarla...
        // Y además, el tenerla definida de forma tradicional me dificulta la lectura del código...
        // ENTONCES ES ME JOR USAR OTRA FORMA QUE TENEMOS DE DEFINIR FUNCIONES: LAS EXPRESIONES LAMBDA, introducidas en JAVA 1.8
        // Mediante un nuevo operador: ->
        // Qué es una expresión lambda: EXPRESION.
        // Qué es una expresión?
        String texto = "Menchu"; // Statement=SENTENCIA (= FRASE, ORACION)
        int numero = 5+6;        // OTRO STATEMENT
                     /// EXPRESION: trozo de código que devuelve un valor
        // Expresión lambda: es un trozo de código que devuelve una función anónima creada / definida dentro de la propia expresión

        Function<String,String> funcionSaludoCortesia = (String nombre) -> {
                                                                                return "Mi estimado/a " + nombre;
                                                                            }; // Se infiere que devuelve un String
        imprimirSaludo(funcionSaludoCortesia, "Menchu");

        imprimirSaludo( (String nombre) -> { return "Mi estimado/a " + nombre; }, "Menchu");
        imprimirSaludo( (nombre) -> { return "Mi estimado/a " + nombre; }, "Menchu");
        imprimirSaludo( nombre -> { return "Mi estimado/a " + nombre; }, "Menchu");
        imprimirSaludo( nombre -> "Mi estimado/a " + nombre, "Menchu");
    }
}