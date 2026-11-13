package sistemapagos.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import sistemapagos.dao.UsuarioDAO;
import sistemapagos.exception.PersistenciaException;
import sistemapagos.model.Usuario;

public class UsuarioDAOMySQL implements UsuarioDAO {

    @Override
    public Optional<Usuario> buscarPorUsernameOCorreo(String valor) {
        String sql = "SELECT id, username, email, password_hash, activo FROM usuarios WHERE username = ? OR email = ?";
        try (Connection c = ConexionMySQL.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setString(2, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setActivo(rs.getBoolean("activo"));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar el usuario.", e);
        }
    }

    @Override
    public long contar() {
        try (Connection c = ConexionMySQL.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM usuarios");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al contar usuarios. ¿Ejecutó database/schema.sql?", e);
        }
    }

    @Override
    public void insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (username, email, password_hash, activo) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexionMySQL.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setBoolean(4, u.isActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el usuario.", e);
        }
    }
}
