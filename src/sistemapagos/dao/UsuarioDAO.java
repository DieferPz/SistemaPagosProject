package sistemapagos.dao;

import java.util.Optional;
import sistemapagos.model.Usuario;

public interface UsuarioDAO {
    /** Busca por nombre de usuario O correo electrónico. */
    Optional<Usuario> buscarPorUsernameOCorreo(String valor);
    long contar();
    void insertar(Usuario usuario);
}
