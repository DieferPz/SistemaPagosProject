package sistemapagos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Resultado registrado de un intento de pago. Guarda solo el id de la venta, no el objeto completo. */
public class Transaccion {
    private int id;
    private String codigo;              // ej. T-1A2B3C4D
    private int ventaId;
    private String ventaCodigo;         // solo lectura: se llena con JOIN al consultar
    private TipoMetodo metodo;
    private BigDecimal importe;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private String motivo;              // motivo del rechazo, si aplica
    private BigDecimal importeRecibido; // solo efectivo
    private BigDecimal cambio;          // solo efectivo
    private String detalle;             // datos SEGUROS del método (tarjeta enmascarada, referencia...)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public int getVentaId() { return ventaId; }
    public void setVentaId(int ventaId) { this.ventaId = ventaId; }
    public String getVentaCodigo() { return ventaCodigo; }
    public void setVentaCodigo(String ventaCodigo) { this.ventaCodigo = ventaCodigo; }
    public TipoMetodo getMetodo() { return metodo; }
    public void setMetodo(TipoMetodo metodo) { this.metodo = metodo; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public BigDecimal getImporteRecibido() { return importeRecibido; }
    public void setImporteRecibido(BigDecimal importeRecibido) { this.importeRecibido = importeRecibido; }
    public BigDecimal getCambio() { return cambio; }
    public void setCambio(BigDecimal cambio) { this.cambio = cambio; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
