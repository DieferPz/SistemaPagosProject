package sistemapagos;

public class PagoTransferencia extends MetodoPagoBase {
    private final String cuentaOrigen;
    private final String referencia;

    public PagoTransferencia(String cuentaOrigen, String referencia) {
        super("Transferencia", 0.0);
        this.cuentaOrigen = cuentaOrigen.trim();
        this.referencia = referencia.trim();
    }

    @Override
    protected boolean autorizar(double monto) {
        return cuentaOrigen.length() >= 8;
    }

    @Override
    public boolean validar(Transaccion transaccion) {
        return referencia.length() >= 6;
    }
}
