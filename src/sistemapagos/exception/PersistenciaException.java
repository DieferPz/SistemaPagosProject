package sistemapagos.exception;

/** Error técnico de conexión, lectura o escritura en MySQL. */
public class PersistenciaException extends SistemaPagosException {
    public PersistenciaException(String mensaje, Throwable causa) { super(mensaje, causa); }
    public PersistenciaException(String mensaje) { super(mensaje); }
}
