package sistemapagos.payment;

import java.math.BigDecimal;
import sistemapagos.model.EstadoPago;

/** Resultado de simular un pago. Solo contiene datos seguros (nunca números completos de tarjeta). */
public record ResultadoPago(EstadoPago estado, String motivo, BigDecimal importeRecibido,
                            BigDecimal cambio, String detalle) {

    public static ResultadoPago aprobado(String detalle) {
        return new ResultadoPago(EstadoPago.APROBADA, null, null, null, detalle);
    }

    public static ResultadoPago rechazado(String motivo, String detalle) {
        return new ResultadoPago(EstadoPago.RECHAZADA, motivo, null, null, detalle);
    }
}
