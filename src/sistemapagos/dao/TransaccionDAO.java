package sistemapagos.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.ResumenFila;
import sistemapagos.model.TipoMetodo;
import sistemapagos.model.Transaccion;

public interface TransaccionDAO {
    /**
     * Guarda la transacción y, si está APROBADA, marca la venta como PAGADA, todo en UNA
     * transacción SQL (commit/rollback). Si la venta ya no está pendiente lanza NegocioException
     * y no guarda nada.
     */
    Transaccion registrarResultado(Transaccion transaccion);

    Optional<Transaccion> buscarPorCodigo(String codigo);

    /**
     * Filtro combinable. Cualquier parámetro en null se ignora.
     * Cubre: por método, por estado, por periodo (desde/hasta inclusive) y por venta.
     */
    List<Transaccion> filtrar(TipoMetodo metodo, EstadoPago estado, LocalDate desde, LocalDate hasta, Integer ventaId);

    /** Cantidad e importe agrupados por método y estado (Proceso 5). */
    List<ResumenFila> resumenPorMetodoYEstado();
}
