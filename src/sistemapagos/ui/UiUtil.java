package sistemapagos.ui;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sistemapagos.exception.SistemaPagosException;

/** Utilidades de interfaz compartidas (mensajes y tablas). */
public final class UiUtil {
    private UiUtil() { }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Ejecuta una acción de la UI mostrando cualquier error como mensaje comprensible.
     * Uso:  UiUtil.ejecutar(this, () -> { servicio.hacerAlgo(); refrescar(); });
     */
    public static void ejecutar(Component padre, Runnable accion) {
        try {
            accion.run();
        } catch (SistemaPagosException e) {
            error(padre, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            error(padre, "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    /** Modelo de tabla de solo lectura. */
    public static DefaultTableModel modeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }
}
