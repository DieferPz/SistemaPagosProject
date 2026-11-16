package sistemapagos.cobros;

import java.math.BigDecimal;

/** Pago en efectivo: registra el monto recibido y calcula el cambio. */
public class CobroEfectivo extends MetodoCobro {

    private final BigDecimal montoRecibido;
    private BigDecimal cambio = BigDecimal.ZERO;

    public CobroEfectivo(BigDecimal montoRecibido) {
        super("Efectivo");
        this.montoRecibido = montoRecibido;
    }

    @Override public String getCodigo() { return "EFECTIVO"; }

    @Override
    public void validarDatos(BigDecimal importeVenta) throws CobroValidacionException {
        if (montoRecibido == null || montoRecibido.signum() <= 0) {
            throw new CobroValidacionException("El monto recibido debe ser mayor que cero.");
        }
        if (montoRecibido.compareTo(importeVenta) < 0) {
            throw new CobroValidacionException("El monto recibido ($" + montoRecibido
                    + ") es insuficiente para cubrir la venta ($" + importeVenta + ").");
        }
        cambio = montoRecibido.subtract(importeVenta); // BigDecimal: sin errores de punto flotante
    }

    @Override
    public Simulacion simular(BigDecimal importeVenta) {
        return new Simulacion(true, null); // el efectivo, si alcanza, siempre se acepta
    }

    @Override public String getDetalle() { return "Pago en efectivo"; }
    @Override public BigDecimal getMontoRecibido() { return montoRecibido; }
    @Override public BigDecimal getCambio() { return cambio; }
}