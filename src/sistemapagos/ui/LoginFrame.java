package sistemapagos.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.BorderFactory;
import java.awt.Color;
import sistemapagos.AppContext;
import sistemapagos.exception.SistemaPagosException;

/** RF-01: pantalla de inicio de sesión. */
public class LoginFrame extends JFrame {
    private final AppContext contexto;
    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JLabel lblMensaje = new JLabel(" ");
    private final JButton btnEntrar = new JButton("Iniciar sesión");

    public LoginFrame(AppContext contexto) {
        super("Sistema de Pagos - Iniciar sesión");
        this.contexto = contexto;
        construir();
    }

    private void construir() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Sistema de Procesamiento de Pagos");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(titulo, g);

        g.gridwidth = 1;
        g.gridy = 1; g.gridx = 0; panel.add(new JLabel("Usuario o correo:"), g);
        g.gridx = 1; panel.add(txtUsuario, g);
        g.gridy = 2; g.gridx = 0; panel.add(new JLabel("Contraseña:"), g);
        g.gridx = 1; panel.add(txtPassword, g);

        lblMensaje.setForeground(new Color(180, 30, 30));
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        panel.add(lblMensaje, g);

        g.gridy = 4; g.anchor = GridBagConstraints.CENTER;
        panel.add(btnEntrar, g);

        btnEntrar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(btnEntrar);   // Enter = iniciar sesión

        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void iniciarSesion() {
        lblMensaje.setText(" ");
        try {
            contexto.auth.login(txtUsuario.getText(), txtPassword.getPassword());
            new MainFrame(contexto).setVisible(true);
            dispose();
        } catch (SistemaPagosException e) {
            lblMensaje.setText(e.getMessage());
            txtPassword.setText("");
        }
    }
}
