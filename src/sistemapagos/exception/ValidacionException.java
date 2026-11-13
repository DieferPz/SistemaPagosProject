package sistemapagos.exception;

/** Datos de entrada inválidos (campo vacío, formato incorrecto, importe <= 0, etc.). */
public class ValidacionException extends SistemaPagosException {
    public ValidacionException(String mensaje) { super(mensaje); }
}
