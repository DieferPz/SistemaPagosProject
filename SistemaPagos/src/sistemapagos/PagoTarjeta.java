package sistemapagos;

public class PagoTarjeta extends MetodoPagoBase {
    private static final double LIMITE_SIMULADO = 5000.0;

    private final String numeroEnmascarado;
    private final boolean numeroValido;
    private final String tipo;

    public PagoTarjeta(String numero, String tipo) {
        super("Tarjeta", 0.03);
        String limpio = numero.replace(" ", "");
        this.numeroValido = limpio.matches("\\d{16}");
        this.numeroEnmascarado = limpio.length() >= 4
                ? "**** **** **** " + limpio.substring(limpio.length() - 4)
                : "****";
        this.tipo = tipo.trim().toUpperCase();
    }

    @Override
    protected boolean autorizar(double monto) {
        return numeroValido && monto <= LIMITE_SIMULADO;
    }

    @Override
    public boolean validar(Transaccion transaccion) {
        return tipo.equals("CREDITO") || tipo.equals("DEBITO");
    }

    public String getNumeroEnmascarado() {
        return numeroEnmascarado;
    }
}
