package sistemapagos;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sistemapagos.dao.mysql.ConexionMySQL;
import sistemapagos.exception.SistemaPagosException;
import sistemapagos.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorado) {
            // se usa el aspecto por defecto
        }
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        try {
            ConexionMySQL.probar();                          // falla con mensaje claro si MySQL no responde
            AppContext contexto = new AppContext();
            if (contexto.auth.crearAdminPorDefectoSiNoHay()) {
                JOptionPane.showMessageDialog(null,
                        "Primera ejecución: se creó el usuario \"admin\" con contraseña \"admin123\".\n"
                        + "Cámbiela cuanto antes.", "Usuario inicial", JOptionPane.INFORMATION_MESSAGE);
            }
            new LoginFrame(contexto).setVisible(true);
        } catch (SistemaPagosException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "No se pudo iniciar", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
