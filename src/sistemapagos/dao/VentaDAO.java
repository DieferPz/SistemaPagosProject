package sistemapagos.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import sistemapagos.model.EstadoVenta;
import sistemapagos.model.Venta;

public interface VentaDAO {
    /** Inserta y devuelve la venta con su id generado. */
    Venta insertar(Venta venta);
    void actualizar(Venta venta);
    /** @return true si se eliminó una fila. */
    boolean eliminar(int id);
    Optional<Venta> buscarPorId(int id);
    Optional<Venta> buscarPorCodigo(String codigo);
    boolean existeCodigo(String codigo);
    List<Venta> listar();
    List<Venta> listarPorEstado(EstadoVenta estado);
    /** Ventas con fecha entre desde y hasta (ambas inclusive). */
    List<Venta> buscarPorFecha(LocalDate desde, LocalDate hasta);
}
