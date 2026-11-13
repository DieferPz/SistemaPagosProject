package sistemapagos.model;

import java.math.BigDecimal;

/** Una fila del resumen: cantidad e importe por (método, estado). */
public record ResumenFila(TipoMetodo metodo, EstadoPago estado, int cantidad, BigDecimal total) { }
