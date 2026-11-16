package sistemapagos.cobros;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Transferencia bancaria (simulada). Se guarda la cuenta enmascarada. */
public class CobroTransferencia extends MetodoCobro {

    private static final Pattern CUENTA = Pattern.compile("^\\d{10,20}$");
    private static final Pattern REFERENCIA = Pattern.compile("^[A-Za-z0-9]{6,20}$");

    private final String banco;
    private final String cuenta;
    private final String referencia;

    public CobroTransferencia(String banco, String cuenta, String referencia) {
        super("Transferencia bancaria");
        this.banco = banco == null ? "" : banco.trim();
        this.cuenta = cuenta == null ? "" : cuenta.trim();
        this.referencia = referencia == null ? "" : referencia.trim();
    }

    @Override public String getCodigo() { return "TRANSFERENCIA"; }

    @Override
    public void validarDatos(BigDecimal importeVenta) throws CobroValidacionException {
        requerirTexto(banco, "Banco");
        requerirTexto(cuenta, "Número de cuenta");
        requerirTexto(referencia, "Referencia");
        if (!CUENTA.matcher(cuenta).matches()) {
            throw new CobroValidacionException("La cuenta debe tener entre 10 y 20 dígitos (solo números).");
        }
        if (!REFERENCIA.matcher(referencia).matches()) {
            throw new CobroValidacionException("La referencia debe ser alfanumérica (6 a 20 caracteres).");
        }
    }

    /** Regla simulada: referencias que empiezan con RECH se rechazan. */
    @Override
    public Simulacion simular(BigDecimal importeVenta) {
        if (referencia.toUpperCase().startsWith("RECH")) {
            return new Simulacion(false, "Transferencia rechazada por el banco (simulado).");
        }
        return new Simulacion(true, null);
    }

    @Override
    public String getDetalle() {
        return "Transferencia " + banco + " cuenta ****" + cuenta.substring(cuenta.length() - 4)
                + " ref " + referencia;
    }
}