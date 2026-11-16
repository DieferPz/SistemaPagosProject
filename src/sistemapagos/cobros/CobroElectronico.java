package sistemapagos.cobros;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Pago electrónico (billetera digital, QR, etc.) simulado. */
public class CobroElectronico extends MetodoCobro {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final String plataforma;
    private final String correo;

    public CobroElectronico(String plataforma, String correo) {
        super("Pago electrónico");
        this.plataforma = plataforma == null ? "" : plataforma.trim();
        this.correo = correo == null ? "" : correo.trim();
    }

    @Override public String getCodigo() { return "ELECTRONICO"; }

    @Override
    public void validarDatos(BigDecimal importeVenta) throws CobroValidacionException {
        requerirTexto(plataforma, "Plataforma");
        requerirTexto(correo, "Correo de la cuenta");
        if (!CORREO.matcher(correo).matches()) {
            throw new CobroValidacionException("El correo no tiene un formato válido (ejemplo: usuario@dominio.com).");
        }
    }

    /** Regla simulada: cuentas del dominio rechazo.test se rechazan. */
    @Override
    public Simulacion simular(BigDecimal importeVenta) {
        if (correo.toLowerCase().endsWith("@rechazo.test")) {
            return new Simulacion(false, "Cuenta electrónica sin autorización (simulado).");
        }
        return new Simulacion(true, null);
    }

    @Override public String getDetalle() { return plataforma + " - " + correo; }
}
