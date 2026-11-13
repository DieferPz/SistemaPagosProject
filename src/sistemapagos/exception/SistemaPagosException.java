package sistemapagos.exception;

/** Base de todas las excepciones de la aplicación. El mensaje siempre es apto para mostrarse al usuario. */
public class SistemaPagosException extends RuntimeException {
    public SistemaPagosException(String mensaje) { super(mensaje); }
    public SistemaPagosException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
