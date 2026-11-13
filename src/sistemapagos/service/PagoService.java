package sistemapagos.service;

import java.time.LocalDateTime;
import java.util.UUID;
import sistemapagos.dao.TransaccionDAO;
import sistemapagos.dao.VentaDAO;
import sistemapagos.exception.NegocioException;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.EstadoVenta;
import sistemapagos.model.Transaccion;
import sistemapagos.model.Venta;
import sistemapagos.payment.MetodoPago;
import sistemapagos.payment.ResultadoPago;
import sistemapagos.security.Sesion;

/** Procesos 2 y 3: método de pago, procesamiento y validación. (Módulo de la persona C) */
public class PagoService {
    private final VentaDAO ventaDAO;
    private final TransaccionDAO transaccionDAO;

    public PagoService(VentaDAO ventaDAO, TransaccionDAO transaccionDAO) {
        this.ventaDAO = ventaDAO;
        this.transaccionDAO = transaccionDAO;
    }

    /**
     * Subprocesos 3.1 a 3.6.
     * - ValidacionException: datos del método incompletos/ inválidos (no se crea transacción).
     * - Transacción RECHAZADA: se registra con su motivo y la venta sigue pendiente.
     * - Transacción APROBADA: se registra y la venta pasa a PAGADA (misma transacción SQL).
     */
    public Transaccion procesarPago(int ventaId, MetodoPago metodo) {
        Sesion.requerirAutenticacion();
        if (metodo == null) {
            throw new ValidacionException("Seleccione un método de pago.");
        }
        // 3.1 recuperar venta
        Venta venta = ventaDAO.buscarPorId(ventaId)
                .orElseThrow(() -> new NegocioException("No se puede procesar el pago: la venta no existe."));
        if (venta.getEstado() == EstadoVenta.PAGADA) {
            throw new NegocioException("La venta " + venta.getCodigo() + " ya fue pagada.");
        }
        // 3.2 el importe del pago es el importe pendiente de la venta; 3.3 y 3.4 los hace el método
        ResultadoPago resultado = metodo.procesar(venta);

        // 3.5 registrar resultado
        Transaccion t = new Transaccion();
        t.setCodigo("T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        t.setVentaId(venta.getId());
        t.setVentaCodigo(venta.getCodigo());
        t.setMetodo(metodo.getTipo());
        t.setImporte(venta.getImporte());
        t.setFecha(LocalDateTime.now());
        t.setEstado(resultado.estado());
        t.setMotivo(resultado.motivo());
        t.setImporteRecibido(resultado.importeRecibido());
        t.setCambio(resultado.cambio());
        t.setDetalle(resultado.detalle());

        // 3.6 actualizar venta (lo hace el DAO dentro de la misma transacción SQL si fue aprobada)
        return transaccionDAO.registrarResultado(t);
    }
}
