package sistemapagos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import sistemapagos.dao.VentaDAO;
import sistemapagos.exception.NegocioException;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.EstadoVenta;
import sistemapagos.model.Venta;
import sistemapagos.security.Sesion;
import sistemapagos.validation.Validador;

/** Proceso 1: gestión de ventas. (Módulo de la persona B) */
public class VentaService {
    private final VentaDAO ventaDAO;

    public VentaService(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    /** 1.1 + 1.2: registrar y validar. */
    public Venta registrar(String codigo, String descripcion, String importeTexto) {
        Sesion.requerirAutenticacion();
        String cod = Validador.requerirTexto(codigo, "Código", 30);
        String desc = Validador.requerirTexto(descripcion, "Descripción", 255);
        BigDecimal importe = Validador.parsearImporte(importeTexto, "Importe");
        if (ventaDAO.existeCodigo(cod)) {
            throw new NegocioException("Ya existe una venta con el código \"" + cod + "\".");
        }
        return ventaDAO.insertar(new Venta(cod, desc, importe, LocalDate.now()));
    }

    /** 1.5: modificar solo si la venta está pendiente. */
    public Venta modificar(int id, String codigo, String descripcion, String importeTexto) {
        Sesion.requerirAutenticacion();
        Venta venta = obtener(id);
        if (!venta.puedeModificarse()) {
            throw new NegocioException("La venta " + venta.getCodigo() + " ya fue pagada y no puede modificarse.");
        }
        String cod = Validador.requerirTexto(codigo, "Código", 30);
        String desc = Validador.requerirTexto(descripcion, "Descripción", 255);
        BigDecimal importe = Validador.parsearImporte(importeTexto, "Importe");
        if (!cod.equals(venta.getCodigo()) && ventaDAO.existeCodigo(cod)) {
            throw new NegocioException("Ya existe una venta con el código \"" + cod + "\".");
        }
        venta.setCodigo(cod);
        venta.setDescripcion(desc);
        venta.setImporte(importe);
        ventaDAO.actualizar(venta);
        return venta;
    }

    /** Eliminar solo si está pendiente y sin transacciones asociadas (la base lo garantiza con FK). */
    public void eliminar(int id) {
        Sesion.requerirAutenticacion();
        Venta venta = obtener(id);
        if (!venta.puedeModificarse()) {
            throw new NegocioException("No se puede eliminar una venta ya pagada.");
        }
        if (!ventaDAO.eliminar(id)) {
            throw new NegocioException("La venta no existe.");
        }
    }

    /** 1.3 */
    public List<Venta> listar() {
        Sesion.requerirAutenticacion();
        return ventaDAO.listar();
    }

    public List<Venta> listarPendientes() {
        Sesion.requerirAutenticacion();
        return ventaDAO.listarPorEstado(EstadoVenta.PENDIENTE);
    }

    /** 1.4 */
    public Venta buscarPorCodigo(String codigo) {
        Sesion.requerirAutenticacion();
        String cod = Validador.requerirTexto(codigo, "Código");
        return ventaDAO.buscarPorCodigo(cod)
                .orElseThrow(() -> new NegocioException("No existe una venta con el código \"" + cod + "\"."));
    }

    /** 1.4 (por fecha o rango). Si hasta es null se usa la misma fecha. */
    public List<Venta> buscarPorFecha(LocalDate desde, LocalDate hasta) {
        Sesion.requerirAutenticacion();
        if (desde == null) throw new ValidacionException("Ingrese la fecha a buscar.");
        LocalDate fin = hasta == null ? desde : hasta;
        if (fin.isBefore(desde)) throw new ValidacionException("La fecha final no puede ser anterior a la inicial.");
        return ventaDAO.buscarPorFecha(desde, fin);
    }

    public Venta obtener(int id) {
        Sesion.requerirAutenticacion();
        return ventaDAO.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("La venta no existe."));
    }
}
