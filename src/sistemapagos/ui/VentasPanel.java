package sistemapagos.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import sistemapagos.model.Venta;
import sistemapagos.service.VentaService;

/**
 * MÓDULO PERSONA B - Proceso 1: Gestión de ventas.
 * Ya lista las ventas. TODO (B):
 *  - Formulario: código, descripción, importe  -> ventaService.registrar(...)
 *  - Botones Modificar / Eliminar (solo ventas pendientes) -> modificar(...) / eliminar(...)
 *  - Búsqueda por código y por fecha -> buscarPorCodigo(...) / buscarPorFecha(...)
 *  - Detalle de venta (importe, fecha y sus transacciones; usar ConsultaService.filtrar(..., ventaId))
 *  - Refrescar la tabla después de cada operación y avisar el resultado con UiUtil.
 */
public class VentasPanel extends JPanel {
    private final VentaService ventaService;
    private final DefaultTableModel modelo =
            UiUtil.modeloTabla("ID", "Código", "Descripción", "Importe", "Fecha", "Estado");
    private final JTable tabla = new JTable(modelo);

    public VentasPanel(VentaService ventaService) {
        this.ventaService = ventaService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> UiUtil.ejecutar(this, this::cargar));
        JPanel acciones = new JPanel();
        acciones.add(btnActualizar);

        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        UiUtil.ejecutar(this, this::cargar);
    }

    private void cargar() {
        modelo.setRowCount(0);
        for (Venta v : ventaService.listar()) {
            modelo.addRow(new Object[]{v.getId(), v.getCodigo(), v.getDescripcion(),
                    v.getImporte(), v.getFecha(), v.getEstado()});
        }
    }
}
