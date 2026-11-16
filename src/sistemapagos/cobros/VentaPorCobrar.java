package sistemapagos.cobros;

import java.math.BigDecimal;
import java.time.LocalDate;

/** VentaPorCobrar registrada (solo lectura para el módulo de pagos). */
public record VentaPorCobrar(int id, String codigo, String descripcion, BigDecimal importe, LocalDate fecha, String estado) {
    @Override
    public String toString() {
        return codigo + " - " + descripcion + " ($" + importe + ")";
    }
}