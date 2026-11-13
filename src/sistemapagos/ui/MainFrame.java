package sistemapagos.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import sistemapagos.AppContext;
import sistemapagos.security.Sesion;

/** Ventana principal: una pestaña por módulo + cierre de sesión (RF-06). */
public class MainFrame extends JFrame {
    private final AppContext contexto;

    public MainFrame(AppContext contexto) {
        super("Sistema de Pagos");
        this.contexto = contexto;
        construir();
    }

    private void construir() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        barra.add(new JLabel("Usuario: " + Sesion.getUsuario().getUsername()), BorderLayout.WEST);
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());
        barra.add(btnSalir, BorderLayout.EAST);

        JTabbedPane pestanas = new JTabbedPane();
        // Cada módulo es de una persona distinta: agreguen/cambien SOLO su pestaña.
        pestanas.addTab("Ventas", new VentasPanel(contexto.ventas));                          // Persona B
        pestanas.addTab("Pagar", new PagoPanel(contexto.ventas, contexto.pagos));             // Persona C
        pestanas.addTab("Transacciones", new TransaccionesPanel(contexto.consultas));         // Persona D
        pestanas.addTab("Resumen", new ResumenPanel(contexto.reportes));                      // Persona D

        getContentPane().add(barra, BorderLayout.NORTH);
        getContentPane().add(pestanas, BorderLayout.CENTER);
        setPreferredSize(new Dimension(950, 560));
        pack();
        setLocationRelativeTo(null);
    }

    private void cerrarSesion() {
        if (!UiUtil.confirmar(this, "¿Desea cerrar la sesión?")) return;
        contexto.auth.logout();
        new LoginFrame(contexto).setVisible(true);
        dispose();
    }
}
