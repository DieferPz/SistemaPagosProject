package sistemapagos.cobros;

import java.math.BigDecimal;

/** Resultado que la interfaz muestra al usuario (3.7). */
public record ResultadoCobro(String idTransaccion, boolean aprobado, String motivo, BigDecimal cambio) { }