package sistemapagos.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import sistemapagos.model.ResumenFila;
import sistemapagos.service.ReporteService;
import sistemapagos.service.ResumenPagos;

/**
 * MÓDULO PERSONA D - Proceso 5: resumen de pagos.
 * Ya muestra filas método/estado y el total aprobado. TODO (D):
 *  - Presentación más clara (por ejemplo una fila por método con columnas Aprobadas / Rechazadas / Pendientes).
 *  - Mensaje cuando no haya transacciones.
 *  - Recordar: los rechazados NO suman a los ingresos aprobados.
 */
public class ResumenPanel extends JPanel {
    private final ReporteService reporteService;
    private final DefaultTableModel modelo = UiUtil.modeloTabla("Método", "Estado", "Cantidad", "Importe");
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblTotal = new JLabel(" ");

    public ResumenPanel(ReporteService reporteService) {
        this.reporteService = reporteService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> UiUtil.ejecutar(this, this::cargar));
        JPanel acciones = new JPanel();
        acciones.add(btnActualizar);

        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
        UiUtil.ejecutar(this, this::cargar);
    }

    private void cargar() {
        ResumenPagos r = reporteService.generarResumen();
        modelo.setRowCount(0);
        for (ResumenFila f : r.filas()) {
            modelo.addRow(new Object[]{f.metodo(), f.estado(), f.cantidad(), f.total()});
        }
        lblTotal.setText("Transacciones: " + r.totalTransacciones() + "   |   Ingresos aprobados: $" + r.totalAprobado());
    }
}
