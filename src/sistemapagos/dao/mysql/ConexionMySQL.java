package sistemapagos.dao.mysql;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import sistemapagos.exception.PersistenciaException;

/**
 * Fábrica de conexiones. Lee db.properties (en la carpeta del proyecto, NO se sube a git).
 * Copie db.properties.example -> db.properties y ajuste usuario/contraseña.
 */
public final class ConexionMySQL {
    private static final String ARCHIVO = "db.properties";
    private static Properties props;

    private ConexionMySQL() { }

    public static Connection obtener() {
        cargarPropiedades();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Falta el driver de MySQL. Agregue libs/mysql-connector-j.jar a las librerías del proyecto.", e);
        }
        try {
            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password", ""));
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a MySQL. Verifique que el servidor esté activo y los datos de db.properties. (" + e.getMessage() + ")", e);
        }
    }

    /** Verifica que la conexión funcione (se usa al arrancar la aplicación). */
    public static void probar() {
        try (Connection c = obtener()) {
            if (!c.isValid(3)) {
                throw new PersistenciaException("La conexión a MySQL no es válida.");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al probar la conexión a MySQL.", e);
        }
    }

    private static synchronized void cargarPropiedades() {
        if (props != null) return;
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(ARCHIVO)) {
            p.load(in);
        } catch (IOException e) {
            throw new PersistenciaException("No se encontró " + ARCHIVO + ". Copie db.properties.example a db.properties y configure su MySQL.", e);
        }
        if (p.getProperty("db.url") == null || p.getProperty("db.user") == null) {
            throw new PersistenciaException(ARCHIVO + " debe definir db.url y db.user.");
        }
        props = p;
    }
}
