package sistemapagos.cobros;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sistemapagos.cobros.CobroPersistenciaException;
import sistemapagos.security.Sesion;

/**
 * Procesa pagos dentro de UNA transacción SQL:
 * bloquear venta -> validar -> simular -> registrar transacción -> actualizar venta -> commit.
 * Si algo falla se hace rollback y la venta queda intacta.
 */
public class CobroService {

    public List<VentaPorCobrar> listarVentasPendientes() throws CobroPersistenciaException {
        Sesion.requerirAutenticacion();   // RF-01: funcionalidad protegida por Login
        String sql = "SELECT id, codigo, descripcion, importe, fecha, estado FROM ventas "
                   + "WHERE estado = 'PENDIENTE' ORDER BY fecha, codigo";
        List<VentaPorCobrar> ventas = new ArrayList<>();
        try (Connection con = ConexionCobros.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ventas.add(mapear(rs));
            }
            return ventas;
        } catch (SQLException e) {
            throw new CobroPersistenciaException("No se pudieron leer las ventas: " + e.getMessage(), e);
        }
    }

    public ResultadoCobro procesarPago(String codigoVenta, BigDecimal importePago, MetodoCobro metodo)
            throws CobroValidacionException, CobroPersistenciaException {

        Sesion.requerirAutenticacion();   // RF-01: funcionalidad protegida por Login
        if (codigoVenta == null || codigoVenta.isBlank()) {
            throw new CobroValidacionException("Debe seleccionar una venta.");
        }
        if (metodo == null) {
            throw new CobroValidacionException("Debe seleccionar un método de pago.");
        }

        Connection con = null;
        try {
            con = ConexionCobros.obtener();
            con.setAutoCommit(false);                         // inicio de transacción

            VentaPorCobrar venta = buscarVentaBloqueada(con, codigoVenta);   // 3.1 Recuperar venta
            if (venta == null) {
                throw new CobroValidacionException("La venta " + codigoVenta + " no existe.");
            }
            if (!"PENDIENTE".equals(venta.estado())) {
                throw new CobroValidacionException("La venta " + codigoVenta + " ya fue pagada.");
            }
            if (importePago == null || importePago.compareTo(venta.importe()) != 0) {  // 3.2 Verificar importe
                throw new CobroValidacionException("El importe del pago debe ser igual al importe pendiente ($"
                        + venta.importe() + ").");
            }

            metodo.validarDatos(venta.importe());                        // 3.3 Validar método
            MetodoCobro.Simulacion sim = metodo.simular(venta.importe()); // 3.4 Procesar simulado

            String idTransaccion = "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            insertarTransaccion(con, idTransaccion, venta, metodo, sim);  // 3.5 Registrar resultado

            if (sim.aprobado()) {                                         // 3.6 Actualizar venta
                actualizarVentaPagada(con, venta.codigo());
            }

            con.commit();                                    // todo o nada
            return new ResultadoCobro(idTransaccion, sim.aprobado(), sim.motivo(), metodo.getCambio());

        } catch (CobroValidacionException e) {
            rollback(con);
            throw e;
        } catch (SQLException e) {
            rollback(con);
            throw new CobroPersistenciaException("Error de base de datos al procesar el pago: " + e.getMessage(), e);
        } finally {
            metodo.limpiarDatosSensibles();
            cerrar(con);
        }
    }

    // ---------- SQL ----------

    private VentaPorCobrar buscarVentaBloqueada(Connection con, String codigo) throws SQLException {
        String sql = "SELECT id, codigo, descripcion, importe, fecha, estado FROM ventas WHERE codigo = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Columnas según el modelo Transaccion del proyecto (verificar contra la tabla real). */
    private void insertarTransaccion(Connection con, String codigoTx, VentaPorCobrar venta, MetodoCobro metodo,
                                     MetodoCobro.Simulacion sim) throws SQLException {
        String sql = "INSERT INTO transacciones (codigo, venta_id, metodo, importe, fecha, estado, motivo, "
                   + "importe_recibido, cambio, detalle) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoTx);
            ps.setInt(2, venta.id());
            ps.setString(3, metodo.getCodigo());
            ps.setBigDecimal(4, venta.importe());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, sim.aprobado() ? "APROBADA" : "RECHAZADA");
            ps.setString(7, sim.motivo());
            ps.setBigDecimal(8, metodo.getMontoRecibido());
            ps.setBigDecimal(9, metodo.getCambio());
            ps.setString(10, metodo.getDetalle());
            ps.executeUpdate();
        }
    }

    private void actualizarVentaPagada(Connection con, String codigo) throws SQLException {
        String sql = "UPDATE ventas SET estado = 'PAGADA' WHERE codigo = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("No se pudo actualizar el estado de la venta " + codigo + ".");
            }
        }
    }

    private VentaPorCobrar mapear(ResultSet rs) throws SQLException {
        return new VentaPorCobrar(rs.getInt("id"), rs.getString("codigo"), rs.getString("descripcion"),
                rs.getBigDecimal("importe"), rs.getDate("fecha").toLocalDate(), rs.getString("estado"));
    }

    private void rollback(Connection con) {
        if (con != null) {
            try { con.rollback(); } catch (SQLException ignorada) { }
        }
    }

    private void cerrar(Connection con) {
        if (con != null) {
            try { con.close(); } catch (SQLException ignorada) { }
        }
    }
}