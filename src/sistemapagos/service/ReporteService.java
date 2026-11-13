package sistemapagos.service;

import java.math.BigDecimal;
import java.util.List;
import sistemapagos.dao.TransaccionDAO;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.ResumenFila;
import sistemapagos.security.Sesion;

/** Proceso 5: resumen de pagos por método y estado. (Módulo de la persona D) */
public class ReporteService {
    private final TransaccionDAO transaccionDAO;

    public ReporteService(TransaccionDAO transaccionDAO) {
        this.transaccionDAO = transaccionDAO;
    }

    public ResumenPagos generarResumen() {
        Sesion.requerirAutenticacion();
        List<ResumenFila> filas = transaccionDAO.resumenPorMetodoYEstado();
        int total = 0;
        BigDecimal aprobado = BigDecimal.ZERO;
        for (ResumenFila f : filas) {
            total += f.cantidad();
            if (f.estado() == EstadoPago.APROBADA) {
                aprobado = aprobado.add(f.total());
            }
        }
        return new ResumenPagos(filas, total, aprobado);
    }
}
