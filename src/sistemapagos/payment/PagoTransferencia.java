package sistemapagos.payment;

import java.math.BigDecimal;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.TipoMetodo;

/**
 * Datos de PRUEBA académicos. Reglas de simulación:
 *  - Cuenta de origen: 8 a 20 dígitos. Referencia: 6 a 20 caracteres alfanuméricos.
 *  - Rechazada si el importe supera LIMITE_SIMULADO.
 */
public class PagoTransferencia extends MetodoPago {
    private static final BigDecimal LIMITE_SIMULADO = new BigDecimal("10000.00");

    private final String cuentaOrigen;
    private final String referencia;

    public PagoTransferencia(String cuentaOrigen, String referencia) {
        this.cuentaOrigen = cuentaOrigen == null ? "" : cuentaOrigen.trim();
        this.referencia = referencia == null ? "" : referencia.trim();
    }

    @Override
    public TipoMetodo getTipo() { return TipoMetodo.TRANSFERENCIA; }

    @Override
    protected void validarDatos(BigDecimal importe) {
        requerir(cuentaOrigen, "Cuenta de origen");
        requerir(referencia, "Referencia");
        if (!cuentaOrigen.matches("\\d{8,20}")) {
            throw new ValidacionException("La cuenta de origen debe tener entre 8 y 20 dígitos.");
        }
        if (!referencia.matches("[A-Za-z0-9]{6,20}")) {
            throw new ValidacionException("La referencia debe tener de 6 a 20 caracteres alfanuméricos.");
        }
    }

    @Override
    protected ResultadoPago simular(BigDecimal importe) {
        String detalle = "Cuenta " + enmascarar(cuentaOrigen) + " | Ref: " + referencia;
        if (importe.compareTo(LIMITE_SIMULADO) > 0) {
            return ResultadoPago.rechazado("El importe excede el límite simulado por transferencia ($" + LIMITE_SIMULADO + ").", detalle);
        }
        return ResultadoPago.aprobado(detalle);
    }
}
