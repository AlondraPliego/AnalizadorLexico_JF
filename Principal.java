package CODIGO;

import jflex.Main;
import jflex.exceptions.SilentExit;

public class Principal {
    public static void main(String[] args) {
        // Usar rutas con barras normales o dobles barras invertidas
        String ruta = "C:/Users/Hp/Documents/NetBeansProjects/PRUEBAFLEX/src/CODIGO/Lexico.flex";
        generarLexico(ruta);
    }
    
    public static void generarLexico(String ruta) {
        try {
            // JFlex.Main.generate() espera un array de Strings
            String[] opciones = { ruta };
            Main.generate(opciones);
            
            System.out.println("Archivo Lexico.flex procesado exitosamente");
        } catch (SilentExit e) {
            System.out.println("Error al procesar el archivo Lexico.flex:");
        }
    }
}