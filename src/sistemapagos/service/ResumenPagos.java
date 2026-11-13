package sistemapagos.service;

import java.math.BigDecimal;
import java.util.List;
import sistemapagos.model.ResumenFila;

/** Resumen de pagos. totalAprobado solo suma transacciones APROBADAS (las rechazadas no son ingreso). */
public record ResumenPagos(List<ResumenFila> filas, int totalTransacciones, BigDecimal totalAprobado) { }
