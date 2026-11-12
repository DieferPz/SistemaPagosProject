package sistemapagos;

public class PagoElectronico extends MetodoPagoBase {
    private final String proveedor;
    private final String token;

    public PagoElectronico(String proveedor, String token) {
        super("Pago electronico", 0.02);
        this.proveedor = proveedor.trim();
        this.token = token.trim();
    }

    @Override
    protected boolean autorizar(double monto) {
        return !proveedor.isEmpty() && !token.isEmpty();
    }

    @Override
    public boolean validar(Transaccion transaccion) {
        return token.startsWith("tok_");
    }
}
