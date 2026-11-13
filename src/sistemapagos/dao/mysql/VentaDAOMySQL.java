package sistemapagos.dao.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sistemapagos.dao.VentaDAO;
import sistemapagos.exception.NegocioException;
import sistemapagos.exception.PersistenciaException;
import sistemapagos.model.EstadoVenta;
import sistemapagos.model.Venta;

public class VentaDAOMySQL implements VentaDAO {
    private static final String SELECT = "SELECT id, codigo, descripcion, importe, fecha, estado FROM ventas";

    @Override
    public Venta insertar(Venta v) {
        String sql = "INSERT INTO ventas (codigo, descripcion, importe, fecha, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConexionMySQL.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getCodigo());
            ps.setString(2, v.getDescripcion());
            ps.setBigDecimal(3, v.getImporte());
            ps.setDate(4, Date.valueOf(v.getFecha()));
            ps.setString(5, v.getEstado().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setId(keys.getInt(1));
            }
            return v;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NegocioException("Ya existe una venta con el código \"" + v.getCodigo() + "\".");
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la venta.", e);
        }
    }

    @Override
    public void actualizar(Venta v) {
        String sql = "UPDATE ventas SET codigo = ?, descripcion = ?, importe = ?, fecha = ?, estado = ? WHERE id = ?";
        try (Connection c = ConexionMySQL.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getCodigo());
            ps.setString(2, v.getDescripcion());
            ps.setBigDecimal(3, v.getImporte());
            ps.setDate(4, Date.valueOf(v.getFecha()));
            ps.setString(5, v.getEstado().name());
            ps.setInt(6, v.getId());
            if (ps.executeUpdate() == 0) {
                throw new NegocioException("La venta no existe.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NegocioException("Ya existe una venta con el código \"" + v.getCodigo() + "\".");
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar la venta.", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection c = ConexionMySQL.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM ventas WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NegocioException("No se puede eliminar la venta porque tiene transacciones asociadas.");
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar la venta.", e);
        }
    }

    @Override
    public Optional<Venta> buscarPorId(int id) {
        return uno(SELECT + " WHERE id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public Optional<Venta> buscarPorCodigo(String codigo) {
        return uno(SELECT + " WHERE codigo = ?", ps -> ps.setString(1, codigo));
    }

    @Override
    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo).isPresent();
    }

    @Override
    public List<Venta> listar() {
        return varias(SELECT + " ORDER BY fecha DESC, id DESC", ps -> { });
    }

    @Override
    public List<Venta> listarPorEstado(EstadoVenta estado) {
        return varias(SELECT + " WHERE estado = ? ORDER BY fecha DESC, id DESC", ps -> ps.setString(1, estado.name()));
    }

    @Override
    public List<Venta> buscarPorFecha(LocalDate desde, LocalDate hasta) {
        return varias(SELECT + " WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC, id DESC", ps -> {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
        });
    }

    // ---- utilidades internas ----
    @FunctionalInterface
    private interface Parametros { void asignar(PreparedStatement ps) throws SQLException; }

    private Optional<Venta> uno(String sql, Parametros p) {
        List<Venta> lista = varias(sql, p);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    private List<Venta> varias(String sql, Parametros p) {
        try (Connection c = ConexionMySQL.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            p.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<Venta> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapear(rs));
                return lista;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar ventas.", e);
        }
    }

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id"));
        v.setCodigo(rs.getString("codigo"));
        v.setDescripcion(rs.getString("descripcion"));
        v.setImporte(rs.getBigDecimal("importe"));
        v.setFecha(rs.getDate("fecha").toLocalDate());
        v.setEstado(EstadoVenta.valueOf(rs.getString("estado")));
        return v;
    }
}
