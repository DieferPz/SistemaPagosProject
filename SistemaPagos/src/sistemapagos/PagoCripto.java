package sistemapagos;

// Ejemplo de extension: se agrega un metodo nuevo SIN modificar
// ServicioPagos, los repositorios ni los otros metodos de pago.
public class PagoCripto extends MetodoPagoBase {
    private final String billetera;
    private final String moneda;

    public PagoCripto(String billetera, String moneda) {
        super("Criptomoneda", 0.01);
        this.billetera = billetera.trim();
        this.moneda = moneda.trim().toUpperCase();
    }

    @Override
    protected boolean autorizar(double monto) {
        return billetera.length() >= 10;
    }

    @Override
    public boolean validar(Transaccion transaccion) {
        return moneda.equals("BTC") || moneda.equals("ETH") || moneda.equals("USDT");
    }
}
