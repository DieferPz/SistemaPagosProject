package sistemapagos.payment;

import java.math.BigDecimal;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.TipoMetodo;
import sistemapagos.model.Venta;

/**
 * Clase base de los métodos de pago (Template Method).
 * procesar() fija el orden de los pasos y NO se puede sobrescribir; cada método define:
 *   1) validarDatos()  -> Subproceso 2.3 (datos específicos completos y con formato válido)
 *   2) simular()       -> Subproceso 3.4 (reglas simuladas de aceptación o rechazo)
 *
 * Regla: el importe del pago ES el importe de la venta (sin comisiones).
 * Regla: nunca se conservan números completos de tarjeta, CVV ni credenciales reales.
 */
public abstract class MetodoPago {

    public abstract TipoMetodo getTipo();

    /** Lanza ValidacionException si los datos del método son incompletos o inválidos. */
    protected abstract void validarDatos(BigDecimal importe);

    /** Aplica las reglas simuladas y decide si el pago es aprobado o rechazado (con motivo). */
    protected abstract ResultadoPago simular(BigDecimal importe);

    public final ResultadoPago procesar(Venta venta) {
        if (venta == null) {
            throw new ValidacionException("No hay una venta seleccionada para pagar.");
        }
        BigDecimal importe = venta.getImporte();
        validarDatos(importe);
        return simular(importe);
    }

    // ---- utilidades comunes para las subclases ----
    protected static String requerir(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El dato \"" + campo + "\" es obligatorio.");
        }
        return valor.trim();
    }

    /** Deja visibles solo los últimos 4 caracteres. */
    protected static String enmascarar(String valor) {
        String limpio = valor == null ? "" : valor.replaceAll("\\s", "");
        if (limpio.length() <= 4) return "****";
        return "****" + limpio.substring(limpio.length() - 4);
    }
}
