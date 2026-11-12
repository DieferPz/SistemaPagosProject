package sistemapagos;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ResumenPagos {
    private final Map<String, Double> totalPorMetodo = new LinkedHashMap<>();
    private int cantidadAprobadas = 0;
    private int cantidadRechazadas = 0;
    private double montoTotal = 0;

    public void agregar(Transaccion t) {
        if (t.getEstado() == EstadoPago.APROBADO) {
            cantidadAprobadas++;
            montoTotal += t.getMonto();
            totalPorMetodo.merge(t.getMetodo(), t.getMonto(), Double::sum);
        } else if (t.getEstado() == EstadoPago.RECHAZADO) {
            cantidadRechazadas++;
        }
    }

    public Map<String, Double> getTotalPorMetodo() { return totalPorMetodo; }
    public int getCantidadAprobadas() { return cantidadAprobadas; }
    public int getCantidadRechazadas() { return cantidadRechazadas; }
    public double getMontoTotal() { return montoTotal; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RESUMEN DE PAGOS =====\n");
        sb.append("Transacciones aprobadas : ").append(cantidadAprobadas).append("\n");
        sb.append("Transacciones rechazadas: ").append(cantidadRechazadas).append("\n");
        sb.append("Total por metodo (solo aprobadas):\n");
        if (totalPorMetodo.isEmpty()) {
            sb.append("  (sin pagos aprobados)\n");
        }
        for (Map.Entry<String, Double> e : totalPorMetodo.entrySet()) {
            sb.append(String.format(Locale.US, "  - %-16s $%10.2f%n", e.getKey(), e.getValue()));
        }
        sb.append(String.format(Locale.US, "MONTO TOTAL RECAUDADO: $%.2f", montoTotal));
        return sb.toString();
    }
}
