package sistemapagos.cobros;

import java.math.BigDecimal;

/**
 * Clase base de los métodos de pago. Contiene la lógica común
 * (validación de importes con BigDecimal) y obliga a cada subclase
 * a definir sus validaciones y su simulación.
 */
public abstract class MetodoCobro {

    /** Resultado de la simulación de procesamiento. */
    public record Simulacion(boolean aprobado, String motivo) { }

    private final String nombre;

    protected MetodoCobro(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    /** Valor guardado en la columna transacciones.metodo. */
    public abstract String getCodigo();

    /** Valida los datos específicos del método contra el importe de la venta. */
    public abstract void validarDatos(BigDecimal importeVenta) throws CobroValidacionException;

    /** Aplica las reglas simuladas de aceptación o rechazo. */
    public abstract Simulacion simular(BigDecimal importeVenta);

    /** Texto seguro para guardar (sin números completos ni códigos de seguridad). */
    public abstract String getDetalle();

    /** Solo el efectivo lo usa. */
    public BigDecimal getMontoRecibido() { return null; }
    public BigDecimal getCambio() { return null; }

    /** Borra datos sensibles en memoria al terminar. */
    public void limpiarDatosSensibles() { }

    // ----- utilidades comunes -----

    /** Convierte texto a BigDecimal validando formato, positivo y máximo 2 decimales. */
    public static BigDecimal parsearMonto(String texto, String campo) throws CobroValidacionException {
        if (texto == null || texto.isBlank()) {
            throw new CobroValidacionException("El campo '" + campo + "' es obligatorio.");
        }
        BigDecimal monto;
        try {
            monto = new BigDecimal(texto.trim());
        } catch (NumberFormatException e) {
            throw new CobroValidacionException("El campo '" + campo + "' debe ser numérico (ejemplo: 25.50).");
        }
        if (monto.signum() <= 0) {
            throw new CobroValidacionException("El campo '" + campo + "' debe ser mayor que cero.");
        }
        if (monto.scale() > 2) {
            throw new CobroValidacionException("El campo '" + campo + "' admite máximo 2 decimales.");
        }
        return monto;
    }

    protected static void requerirTexto(String valor, String campo) throws CobroValidacionException {
        if (valor == null || valor.isBlank()) {
            throw new CobroValidacionException("El campo '" + campo + "' es obligatorio.");
        }
    }
}