package sistemapagos.cobros;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.regex.Pattern;

/** Pago con tarjeta (simulado). Solo se conservan los últimos 4 dígitos. */
public class CobroTarjeta extends MetodoCobro {

    private static final Pattern NUMERO = Pattern.compile("^\\d{16}$");
    private static final Pattern VENCIMIENTO = Pattern.compile("^(0[1-9]|1[0-2])/\\d{2}$");
    private static final Pattern CVV = Pattern.compile("^\\d{3,4}$");
    private static final Pattern TITULAR = Pattern.compile("^[\\p{L} .'-]{3,60}$");

    private String numero;
    private String cvv;
    private final String titular;
    private final String vencimiento;
    private String ultimos4 = "";

    public CobroTarjeta(String numero, String titular, String vencimiento, String cvv) {
        super("Tarjeta");
        this.numero = numero == null ? "" : numero.replace(" ", "").trim();
        this.titular = titular == null ? "" : titular.trim();
        this.vencimiento = vencimiento == null ? "" : vencimiento.trim();
        this.cvv = cvv == null ? "" : cvv.trim();
    }

    @Override public String getCodigo() { return "TARJETA"; }

    @Override
    public void validarDatos(BigDecimal importeVenta) throws CobroValidacionException {
        requerirTexto(numero, "Número de tarjeta");
        requerirTexto(titular, "Titular");
        requerirTexto(vencimiento, "Vencimiento");
        requerirTexto(cvv, "CVV");

        if (!NUMERO.matcher(numero).matches()) {
            throw new CobroValidacionException("El número de tarjeta debe tener 16 dígitos (solo números).");
        }
        if (!TITULAR.matcher(titular).matches()) {
            throw new CobroValidacionException("El titular debe contener solo letras (mínimo 3 caracteres).");
        }
        if (!VENCIMIENTO.matcher(vencimiento).matches()) {
            throw new CobroValidacionException("El vencimiento debe tener el formato MM/AA (ejemplo: 08/28).");
        }
        int mes = Integer.parseInt(vencimiento.substring(0, 2));
        int anio = 2000 + Integer.parseInt(vencimiento.substring(3, 5));
        if (YearMonth.of(anio, mes).isBefore(YearMonth.now())) {
            throw new CobroValidacionException("La tarjeta está vencida.");
        }
        if (!CVV.matcher(cvv).matches()) {
            throw new CobroValidacionException("El CVV debe tener 3 o 4 dígitos.");
        }
        ultimos4 = numero.substring(12);
    }

    /** Regla simulada: tarjetas terminadas en 0000 se rechazan. */
    @Override
    public Simulacion simular(BigDecimal importeVenta) {
        if (ultimos4.equals("0000")) {
            return new Simulacion(false, "Fondos insuficientes (simulado).");
        }
        return new Simulacion(true, null);
    }

    @Override public String getDetalle() { return "Tarjeta ****" + ultimos4 + " - " + titular; }

    @Override
    public void limpiarDatosSensibles() {
        numero = "";
        cvv = "";
    }
}