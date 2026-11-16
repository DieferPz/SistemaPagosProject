
package sistemapagos.cobros;

/** Error de validación de datos ingresados por el usuario. */
public class CobroValidacionException extends Exception {
    public CobroValidacionException(String mensaje) {
        super(mensaje);
    }
}
