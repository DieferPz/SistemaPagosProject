package sistemapagos.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Venta con importe total único (Proceso 1). Se asocia a una transacción de pago principal. */
public class Venta {
    private int id;                 // clave interna (AUTO_INCREMENT)
    private String codigo;          // código único visible, lo ingresa el usuario
    private String descripcion;
    private BigDecimal importe;
    private LocalDate fecha;
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    public Venta() { }

    public Venta(String codigo, String descripcion, BigDecimal importe, LocalDate fecha) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.importe = importe;
        this.fecha = fecha;
    }

    /** Regla 1.5: solo se puede modificar/eliminar mientras esté pendiente de pago. */
    public boolean puedeModificarse() { return estado == EstadoVenta.PENDIENTE; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }

    @Override
    public String toString() { return codigo + " - " + descripcion + " ($" + importe + ")"; }
}
