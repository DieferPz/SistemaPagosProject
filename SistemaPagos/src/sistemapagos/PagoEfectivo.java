package sistemapagos;

public class PagoEfectivo extends MetodoPagoBase {
    private final double montoRecibido;

    public PagoEfectivo(double montoRecibido) {
        super("Efectivo", 0.0);
        this.montoRecibido = montoRecibido;
    }

    @Override
    protected boolean autorizar(double monto) {
        return montoRecibido >= monto;
    }

    @Override
    public boolean validar(Transaccion transaccion) {
        return transaccion.getMonto() > 0;
    }
}
