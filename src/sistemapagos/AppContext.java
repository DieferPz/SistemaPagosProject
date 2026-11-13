package sistemapagos;

import sistemapagos.dao.TransaccionDAO;
import sistemapagos.dao.UsuarioDAO;
import sistemapagos.dao.VentaDAO;
import sistemapagos.dao.mysql.TransaccionDAOMySQL;
import sistemapagos.dao.mysql.UsuarioDAOMySQL;
import sistemapagos.dao.mysql.VentaDAOMySQL;
import sistemapagos.service.AuthService;
import sistemapagos.service.ConsultaService;
import sistemapagos.service.PagoService;
import sistemapagos.service.ReporteService;
import sistemapagos.service.VentaService;

/** Punto único donde se "cablean" DAOs y servicios. Las pantallas reciben los servicios desde aquí. */
public final class AppContext {
    public final AuthService auth;
    public final VentaService ventas;
    public final PagoService pagos;
    public final ConsultaService consultas;
    public final ReporteService reportes;

    public AppContext() {
        UsuarioDAO usuarioDAO = new UsuarioDAOMySQL();
        VentaDAO ventaDAO = new VentaDAOMySQL();
        TransaccionDAO transaccionDAO = new TransaccionDAOMySQL();

        this.auth = new AuthService(usuarioDAO);
        this.ventas = new VentaService(ventaDAO);
        this.pagos = new PagoService(ventaDAO, transaccionDAO);
        this.consultas = new ConsultaService(transaccionDAO);
        this.reportes = new ReporteService(transaccionDAO);
    }
}
