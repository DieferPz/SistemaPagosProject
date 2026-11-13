package sistemapagos.service;

import java.util.Arrays;
import java.util.Optional;
import sistemapagos.dao.UsuarioDAO;
import sistemapagos.exception.ValidacionException;
import sistemapagos.exception.NegocioException;
import sistemapagos.model.Usuario;
import sistemapagos.security.PasswordHasher;
import sistemapagos.security.Sesion;

/** RF-01 y RF-06: inicio y cierre de sesión. */
public class AuthService {
    public static final String ADMIN_USER = "admin";
    public static final String ADMIN_PASS = "admin123";   // SOLO para la primera ejecución; cámbiela

    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** @param password se borra de memoria al terminar. */
    public Usuario login(String usuarioOCorreo, char[] password) {
        try {
            if (usuarioOCorreo == null || usuarioOCorreo.trim().isEmpty()) {
                throw new ValidacionException("Ingrese su usuario o correo electrónico.");
            }
            if (password == null || password.length == 0) {
                throw new ValidacionException("Ingrese su contraseña.");
            }
            Optional<Usuario> encontrado = usuarioDAO.buscarPorUsernameOCorreo(usuarioOCorreo.trim());
            // Mensaje genérico: no revela si el usuario existe o si la contraseña fue la incorrecta.
            if (encontrado.isEmpty()
                    || !encontrado.get().isActivo()
                    || !PasswordHasher.verificar(password, encontrado.get().getPasswordHash())) {
                throw new NegocioException("Usuario o contraseña incorrectos.");
            }
            Usuario u = encontrado.get();
            Sesion.iniciar(u);
            return u;
        } finally {
            if (password != null) Arrays.fill(password, '\0');
        }
    }

    public void logout() {
        Sesion.cerrar();
    }

    /** Si la tabla usuarios está vacía crea el usuario admin inicial. @return true si lo creó. */
    public boolean crearAdminPorDefectoSiNoHay() {
        if (usuarioDAO.contar() > 0) return false;
        Usuario admin = new Usuario(ADMIN_USER, "admin@sistemapagos.local",
                PasswordHasher.hash(ADMIN_PASS.toCharArray()));
        usuarioDAO.insertar(admin);
        return true;
    }
}
