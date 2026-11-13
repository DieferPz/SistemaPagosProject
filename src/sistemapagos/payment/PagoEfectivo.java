package sistemapagos.payment;

import java.math.BigDecimal;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.TipoMetodo;

public class PagoEfectivo extends MetodoPago {
    private final BigDecimal importeRecibido;

    public PagoEfectivo(BigDecimal importeRecibido) {
        this.importeRecibido = importeRecibido;
    }

    @Override
    public TipoMetodo getTipo() { return TipoMetodo.EFECTIVO; }

    @Override
    protected void validarDatos(BigDecimal importe) {
        if (importeRecibido == null || importeRecibido.signum() <= 0) {
            throw new ValidacionException("Ingrese el importe recibido en efectivo (mayor que cero).");
        }
        if (importeRecibido.compareTo(importe) < 0) {
            throw new ValidacionException("Importe recibido insuficiente: se requieren $" + importe
                    + " y se recibieron $" + importeRecibido + ".");
        }
    }

    @Override
    protected ResultadoPago simular(BigDecimal importe) {
        BigDecimal cambio = importeRecibido.subtract(importe);
        return new ResultadoPago(EstadoPago.APROBADA, null, importeRecibido, cambio, "Pago en efectivo");
    }
}
