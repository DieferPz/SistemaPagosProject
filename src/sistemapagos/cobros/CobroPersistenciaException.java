package sistemapagos.cobros;

/** Error técnico durante el procesamiento (conexión, lectura o escritura en MySQL). */
public class CobroPersistenciaException extends Exception {
    public CobroPersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}