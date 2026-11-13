package sistemapagos.exception;

/** Violación de una regla de negocio (venta inexistente, venta ya pagada, código duplicado, etc.). */
public class NegocioException extends SistemaPagosException {
    public NegocioException(String mensaje) { super(mensaje); }
}
