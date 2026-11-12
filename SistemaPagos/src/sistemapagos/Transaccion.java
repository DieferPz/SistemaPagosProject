package sistemapagos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transaccion {
    private static int contador = 0;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String id;
    private final Venta venta;
    private final String metodo;
    private final double monto;
    private final LocalDateTime fecha;
    private EstadoPago estado;

    public Transaccion(Venta venta, String metodo, double monto) {
        this.id = String.format("T-%03d", ++contador);
        this.venta = venta;
        this.metodo = metodo;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
    }

    public String getId() { return id; }
    public Venta getVenta() { return venta; }
    public String getMetodo() { return metodo; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | %s | %-16s | $%9.2f | %-9s | %s",
                id, venta.getId(), metodo, monto, estado, fecha.format(FORMATO));
    }
}
