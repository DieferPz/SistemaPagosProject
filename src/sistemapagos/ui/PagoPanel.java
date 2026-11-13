package sistemapagos.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sistemapagos.service.PagoService;
import sistemapagos.service.VentaService;

/**
 * MÓDULO PERSONA C - Procesos 2 y 3: método de pago y procesamiento.
 * TODO (C):
 *  - JComboBox de ventas pendientes (ventaService.listarPendientes()) y de TipoMetodo.
 *  - Mostrar los campos específicos según el método elegido (CardLayout o paneles dinámicos):
 *      Efectivo -> importe recibido | Tarjeta -> número y tipo | Transferencia -> cuenta y referencia
 *      Pago electrónico -> proveedor y token
 *  - Botón "Pagar": crear el MetodoPago correspondiente y llamar pagoService.procesarPago(ventaId, metodo).
 *  - Mostrar resultado (APROBADA/RECHAZADA + motivo + cambio en efectivo) con JOptionPane.
 *  - Limpiar el formulario y refrescar la lista de ventas pendientes.
 */
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
