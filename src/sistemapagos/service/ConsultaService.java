package sistemapagos.service;

import java.time.LocalDate;
import java.util.List;
import sistemapagos.dao.TransaccionDAO;
import sistemapagos.exception.NegocioException;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.TipoMetodo;
import sistemapagos.model.Transaccion;
import sistemapagos.security.Sesion;
import sistemapagos.validation.Validador;

/** Proceso 4: consulta de transacciones. (Módulo de la persona D) */
public class ConsultaService {
    private final TransaccionDAO transaccionDAO;

    public ConsultaService(TransaccionDAO transaccionDAO) {
        this.transaccionDAO = transaccionDAO;
    }

    /** 4.1 */
    public Transaccion buscarPorCodigo(String codigo) {
        Sesion.requerirAutenticacion();
        String cod = Validador.requerirTexto(codigo, "Código de transacción");
        return transaccionDAO.buscarPorCodigo(cod)
                .orElseThrow(() -> new NegocioException("No existe la transacción \"" + cod + "\"."));
    }

    /** 4.2, 4.3, 4.4 y 4.5: cualquier combinación de filtros (null = sin filtrar). */
    public List<Transaccion> filtrar(TipoMetodo metodo, EstadoPago estado, LocalDate desde, LocalDate hasta, Integer ventaId) {
        Sesion.requerirAutenticacion();
        if (desde != null && hasta != null && hasta.isBefore(desde)) {
            throw new ValidacionException("La fecha final no puede ser anterior a la inicial.");
        }
        return transaccionDAO.filtrar(metodo, estado, desde, hasta, ventaId);
    }

    public List<Transaccion> listarTodas() {
        return filtrar(null, null, null, null, null);
    }
}
