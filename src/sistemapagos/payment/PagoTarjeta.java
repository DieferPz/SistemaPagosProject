package sistemapagos.payment;

import java.math.BigDecimal;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.TipoMetodo;

/**
 * Datos de PRUEBA académicos. Reglas de simulación:
 *  - Número de 16 dígitos y tipo CREDITO o DEBITO.
 *  - Rechazada si el importe supera LIMITE_SIMULADO.
 *  - Rechazada si el número termina en 0000 (para demostrar un rechazo).
 * Solo se guarda el número enmascarado (últimos 4 dígitos); jamás el número completo ni el CVV.
 */
public class PagoTarjeta extends MetodoPago {
    private static final BigDecimal LIMITE_SIMULADO = new BigDecimal("5000.00");

    private final boolean numeroValido;
    private final boolean terminaEnCeros;
    private final String enmascarado;
    private final String tipo;

    public PagoTarjeta(String numero, String tipo) {
        String limpio = numero == null ? "" : numero.replaceAll("\\s", "");
        this.numeroValido = limpio.matches("\\d{16}");
        this.terminaEnCeros = limpio.endsWith("0000");
        this.enmascarado = enmascarar(limpio);
        this.tipo = tipo == null ? "" : tipo.trim().toUpperCase();
    }

    @Override
    public TipoMetodo getTipo() { return TipoMetodo.TARJETA; }

    @Override
    protected void validarDatos(BigDecimal importe) {
        if (!numeroValido) {
            throw new ValidacionException("El número de tarjeta debe tener 16 dígitos.");
        }
        if (!tipo.equals("CREDITO") && !tipo.equals("DEBITO")) {
            throw new ValidacionException("Seleccione el tipo de tarjeta: CREDITO o DEBITO.");
        }
    }

    @Override
    protected ResultadoPago simular(BigDecimal importe) {
        String detalle = "Tarjeta " + enmascarado + " (" + tipo + ")";
        if (importe.compareTo(LIMITE_SIMULADO) > 0) {
            return ResultadoPago.rechazado("El importe excede el límite simulado de $" + LIMITE_SIMULADO + ".", detalle);
        }
        if (terminaEnCeros) {
            return ResultadoPago.rechazado("Tarjeta rechazada por el emisor (simulado).", detalle);
        }
        return ResultadoPago.aprobado(detalle);
    }
}
