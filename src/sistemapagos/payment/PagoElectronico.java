package sistemapagos.payment;

import java.math.BigDecimal;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.TipoMetodo;

/**
 * Datos de PRUEBA académicos. Reglas de simulación:
 *  - Proveedor obligatorio. Token con formato tok_XXXXXXXX (mínimo 8 caracteres en total).
 *  - Rechazado si el token termina en "_fail" (para demostrar un rechazo).
 */
public class PagoElectronico extends MetodoPago {
    private final String proveedor;
    private final String token;

    public PagoElectronico(String proveedor, String token) {
        this.proveedor = proveedor == null ? "" : proveedor.trim();
        this.token = token == null ? "" : token.trim();
    }

    @Override
    public TipoMetodo getTipo() { return TipoMetodo.PAGO_ELECTRONICO; }

    @Override
    protected void validarDatos(BigDecimal importe) {
        requerir(proveedor, "Proveedor");
        requerir(token, "Token");
        if (!token.startsWith("tok_") || token.length() < 8) {
            throw new ValidacionException("El token debe iniciar con \"tok_\" y tener al menos 8 caracteres.");
        }
    }

    @Override
    protected ResultadoPago simular(BigDecimal importe) {
        String detalle = "Proveedor " + proveedor + " | Token " + enmascarar(token);
        if (token.endsWith("_fail")) {
            return ResultadoPago.rechazado("Rechazado por el proveedor (simulado).", detalle);
        }
        return ResultadoPago.aprobado(detalle);
    }
}
