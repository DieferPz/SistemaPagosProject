
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sistemapagos.service.PagoService;
import sistemapagos.service.VentaService;

public class PagoPanel extends JPanel {
    private final VentaService ventaService;
    private final PagoService pagoService;

    public PagoPanel(VentaService ventaService, PagoService pagoService) {
        this.ventaService = ventaService;
        this.pagoService = pagoService;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JLabel("Módulo de pagos - pendiente de implementar (Persona C)"), BorderLayout.NORTH);
    }
}
