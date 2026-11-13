package sistemapagos.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import sistemapagos.model.Transaccion;
import sistemapagos.service.ConsultaService;

/**
 * MÓDULO PERSONA D - Proceso 4: consulta de transacciones.
 * Ya lista todas. TODO (D):
 *  - Buscar por código de transacción (4.1) -> consultaService.buscarPorCodigo(...)
 *  - Filtros con JComboBox: método (4.2) y estado (4.3), más fechas desde/hasta (4.4)
 *  - Filtrar por venta (4.5)
 *  - Todo se resuelve con consultaService.filtrar(metodo, estado, desde, hasta, ventaId)
 *  - Mensaje claro cuando la consulta no devuelva resultados.
 */
public class TransaccionesPanel extends JPanel {
    private final ConsultaService consultaService;
    private final DefaultTableModel modelo = UiUtil.modeloTabla(
            "Código", "Venta", "Método", "Importe", "Fecha", "Estado", "Motivo", "Detalle");
    private final JTable tabla = new JTable(modelo);

    public TransaccionesPanel(ConsultaService consultaService) {
        this.consultaService = consultaService;
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
        for (Transaccion t : consultaService.listarTodas()) {
            modelo.addRow(new Object[]{t.getCodigo(), t.getVentaCodigo(), t.getMetodo(), t.getImporte(),
                    t.getFecha(), t.getEstado(), t.getMotivo(), t.getDetalle()});
        }
    }
}
