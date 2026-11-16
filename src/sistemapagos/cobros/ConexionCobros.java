package sistemapagos.cobros;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión a MySQL para el módulo de cobros.
 * Lee los datos de db.properties (el mismo archivo del proyecto, que NO se sube a Git).
 * Acepta varios nombres de clave comunes; si el archivo no existe usa variables de entorno
 * DB_URL, DB_USER y DB_PASS, y por último valores por defecto sin contraseña.
 */
public final class ConexionCobros {

    private static final String[] CLAVES_URL = {"db.url", "url", "jdbc.url", "database.url"};
    private static final String[] CLAVES_USUARIO = {"db.user", "db.usuario", "user", "usuario", "username", "db.username"};
    private static final String[] CLAVES_CLAVE = {"db.password", "db.pass", "password", "pass", "contrasena", "clave", "db.clave"};

    private static final Properties PROPS = cargar();

    private ConexionCobros() { }

    public static Connection obtener() throws SQLException {
        String url = valor(CLAVES_URL, "DB_URL",
                "jdbc:mysql://localhost:3306/sistema_pagos?useSSL=false&allowPublicKeyRetrieval=true");
        String usuario = valor(CLAVES_USUARIO, "DB_USER", "root");
        String clave = valor(CLAVES_CLAVE, "DB_PASS", "");
        return DriverManager.getConnection(url, usuario, clave);
    }

    private static Properties cargar() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (IOException e) {
            // Sin archivo: se usarán variables de entorno o valores por defecto.
        }
        return p;
    }

    private static String valor(String[] claves, String variableEntorno, String porDefecto) {
        for (String clave : claves) {
            String v = PROPS.getProperty(clave);
            if (v != null) {
                return v.trim();
            }
        }
        String env = System.getenv(variableEntorno);
        return (env == null || env.isBlank()) ? porDefecto : env;
    }
}