package sistemapagos.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import sistemapagos.cobros.CobroPanel;
import sistemapagos.service.PagoService;
import sistemapagos.service.VentaService;

/**
 * MÓDULO PERSONA C - Procesos 2 y 3: método de pago y procesamiento.
 * Muestra el panel de cobros (paquete sistemapagos.cobros).
 */
public class PagoPanel extends JPanel {

    private final VentaService ventaService;
    private final PagoService pagoService;

    public PagoPanel(VentaService ventaService, PagoService pagoService) {
        this.ventaService = ventaService;
        this.pagoService = pagoService;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new CobroPanel(), BorderLayout.CENTER);
    }
}
