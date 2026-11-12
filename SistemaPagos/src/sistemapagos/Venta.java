package sistemapagos;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class Venta {
    private static int contador = 0;

    private final String id;
    private final LocalDateTime fecha;
    private final Map<String, Double> items;
    private final double total;
    private boolean pagada;

    public Venta(Map<String, Double> items) {
        this.id = String.format("V-%03d", ++contador); //Generador de codigos con la forma V-000 con enteros
        this.fecha = LocalDateTime.now(); // La fecha de generacion
        this.items = new LinkedHashMap<>(items); 
        double suma = 0;
        for (double precio : items.values()) {
            suma += precio;
        }
        this.total = suma;
        this.pagada = false;
    }

    public String getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Map<String, Double> getItems() { return items; }
    public double getTotal() { return total; }
    public boolean isPagada() { return pagada; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "%s | %d item(s) | Total: $%.2f | %s",
                id, items.size(), total, pagada ? "PAGADA" : "PENDIENTE DE PAGO");
    }
}
