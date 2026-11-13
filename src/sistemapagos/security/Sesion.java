package sistemapagos.security;

import sistemapagos.exception.NegocioException;
import sistemapagos.model.Usuario;

/** Sesión de la aplicación (un solo usuario a la vez). Protege las funcionalidades hasta iniciar sesión. */
public final class Sesion {
    private static Usuario usuarioActual;

    private Sesion() { }

    public static void iniciar(Usuario u) { usuarioActual = u; }
    public static void cerrar() { usuarioActual = null; }
    public static boolean estaAutenticado() { return usuarioActual != null; }
    public static Usuario getUsuario() { return usuarioActual; }

    /** Los servicios la llaman al inicio de cada operación protegida (RF-01 / RF-06). */
    public static void requerirAutenticacion() {
        if (usuarioActual == null) {
            throw new NegocioException("Debe iniciar sesión para realizar esta operación.");
        }
    }
}
