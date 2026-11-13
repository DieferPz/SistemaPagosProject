package sistemapagos.dao.mysql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sistemapagos.dao.TransaccionDAO;
import sistemapagos.exception.NegocioException;
import sistemapagos.exception.PersistenciaException;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.ResumenFila;
import sistemapagos.model.TipoMetodo;
import sistemapagos.model.Transaccion;

public class TransaccionDAOMySQL implements TransaccionDAO {

    private static final String SELECT =
            "SELECT t.id, t.codigo, t.venta_id, v.codigo AS venta_codigo, t.metodo, t.importe, t.fecha, "
            + "t.estado, t.motivo, t.importe_recibido, t.cambio, t.detalle "
            + "FROM transacciones t JOIN ventas v ON v.id = t.venta_id";

    @Override
    public Transaccion registrarResultado(Transaccion t) {
        String insert = "INSERT INTO transacciones (codigo, venta_id, metodo, importe, fecha, estado, motivo, "
                + "importe_recibido, cambio, detalle) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String pagarVenta = "UPDATE ventas SET estado = 'PAGADA' WHERE id = ? AND estado = 'PENDIENTE'";

        try (Connection c = ConexionMySQL.obtener()) {
            c.setAutoCommit(false);
            try {
                // 1) Si es aprobada, primero se "reclama" la venta: si ya no está pendiente, 0 filas -> se aborta.
                if (t.getEstado() == EstadoPago.APROBADA) {
                    try (PreparedStatement ps = c.prepareStatement(pagarVenta)) {
                        ps.setInt(1, t.getVentaId());
                        if (ps.executeUpdate() == 0) {
                            throw new NegocioException("La venta ya fue pagada o no existe.");
                        }
                    }
                }
                // 2) Se guarda la transacción (aprobada o rechazada).
                try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, t.getCodigo());
                    ps.setInt(2, t.getVentaId());
                    ps.setString(3, t.getMetodo().name());
                    ps.setBigDecimal(4, t.getImporte());
                    ps.setTimestamp(5, Timestamp.valueOf(t.getFecha()));
                    ps.setString(6, t.getEstado().name());
                    ps.setString(7, t.getMotivo());
                    ps.setBigDecimal(8, t.getImporteRecibido());
                    ps.setBigDecimal(9, t.getCambio());
                    ps.setString(10, t.getDetalle());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) t.setId(keys.getInt(1));
                    }
                }
                c.commit();
                return t;
            } catch (NegocioException e) {
                c.rollback();
                throw e;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar la transacción. No se realizaron cambios.", e);
        }
    }

    @Override
    public Optional<Transaccion> buscarPorCodigo(String codigo) {
        List<Transaccion> lista = consultar(SELECT + " WHERE t.codigo = ?", List.of(codigo));
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Transaccion> filtrar(TipoMetodo metodo, EstadoPago estado, LocalDate desde, LocalDate hasta, Integer ventaId) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (metodo != null)  { sql.append(" AND t.metodo = ?");  params.add(metodo.name()); }
        if (estado != null)  { sql.append(" AND t.estado = ?");  params.add(estado.name()); }
        if (desde != null)   { sql.append(" AND t.fecha >= ?");  params.add(Timestamp.valueOf(desde.atStartOfDay())); }
        if (hasta != null)   { sql.append(" AND t.fecha < ?");   params.add(Timestamp.valueOf(hasta.plusDays(1).atStartOfDay())); }
        if (ventaId != null) { sql.append(" AND t.venta_id = ?"); params.add(ventaId); }
        sql.append(" ORDER BY t.fecha DESC, t.id DESC");
        return consultar(sql.toString(), params);
    }

    @Override
    public List<ResumenFila> resumenPorMetodoYEstado() {
        String sql = "SELECT metodo, estado, COUNT(*) AS cantidad, COALESCE(SUM(importe), 0) AS total "
                + "FROM transacciones GROUP BY metodo, estado ORDER BY metodo, estado";
        try (Connection c = ConexionMySQL.obtener();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ResumenFila> filas = new ArrayList<>();
            while (rs.next()) {
                filas.add(new ResumenFila(
                        TipoMetodo.valueOf(rs.getString("metodo")),
                        EstadoPago.valueOf(rs.getString("estado")),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("total")));
            }
            return filas;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al generar el resumen de pagos.", e);
        }
    }

    // ---- utilidades internas ----
    private List<Transaccion> consultar(String sql, List<?> params) {
        try (Connection c = ConexionMySQL.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Transaccion> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapear(rs));
                return lista;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar transacciones.", e);
        }
    }

    private Transaccion mapear(ResultSet rs) throws SQLException {
        Transaccion t = new Transaccion();
        t.setId(rs.getInt("id"));
        t.setCodigo(rs.getString("codigo"));
        t.setVentaId(rs.getInt("venta_id"));
        t.setVentaCodigo(rs.getString("venta_codigo"));
        t.setMetodo(TipoMetodo.valueOf(rs.getString("metodo")));
        t.setImporte(rs.getBigDecimal("importe"));
        t.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        t.setEstado(EstadoPago.valueOf(rs.getString("estado")));
        t.setMotivo(rs.getString("motivo"));
        BigDecimal recibido = rs.getBigDecimal("importe_recibido");
        t.setImporteRecibido(recibido);
        t.setCambio(rs.getBigDecimal("cambio"));
        t.setDetalle(rs.getString("detalle"));
        return t;
    }
}
