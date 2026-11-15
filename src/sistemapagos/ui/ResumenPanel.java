package sistemapagos.ui;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.ResumenFila;
import sistemapagos.model.TipoMetodo;
import sistemapagos.service.ReporteService;
import sistemapagos.service.ResumenPagos;

/**
 * BILLY - Proceso: resumen de pagos.
 */
public class ResumenPanel extends JPanel {
    private final ReporteService reporteService;
    private final DefaultTableModel modelo = UiUtil.modeloTabla(
            "Método de Pago", "Aprobadas", "Ingresos Aprobados ($)", "Rechazadas", "Pendientes", "Total Transacciones");
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblTotal = new JLabel(" ");

    private static class AcumuladoMetodo {
        int aprobadas = 0;
        BigDecimal montoAprobado = BigDecimal.ZERO;
        int rechazadas = 0;
        int pendientes = 0;

        int totalTransacciones() {
            return aprobadas + rechazadas + pendientes;
        }
    }

    public ResumenPanel(ReporteService reporteService) {
        this.reporteService = reporteService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnActualizar = new JButton("Actualizar resumen");
        btnActualizar.addActionListener(e -> UiUtil.ejecutar(this, () -> cargar(true)));
        JPanel acciones = new JPanel();
        acciones.add(btnActualizar);

        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);

        UiUtil.ejecutar(this, () -> cargar(false));
    }

    private void cargar(boolean notificarSiVacio) {
        ResumenPagos r = reporteService.generarResumen();
        modelo.setRowCount(0);

        if (r.totalTransacciones() == 0) {
            lblTotal.setText("No hay transacciones registradas aún. Registre un pago para ver el resumen.");
            if (notificarSiVacio) {
                JOptionPane.showMessageDialog(this,
                        "Aún no existen transacciones registradas en el sistema.",
                        "Resumen sin datos",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        Map<TipoMetodo, AcumuladoMetodo> mapa = new EnumMap<>(TipoMetodo.class);
        for (TipoMetodo m : TipoMetodo.values()) {
            mapa.put(m, new AcumuladoMetodo());
        }

        for (ResumenFila f : r.filas()) {
            AcumuladoMetodo ac = mapa.computeIfAbsent(f.metodo(), k -> new AcumuladoMetodo());
            if (f.estado() == EstadoPago.APROBADA) {
                ac.aprobadas += f.cantidad();
                ac.montoAprobado = ac.montoAprobado.add(f.total());
            } else if (f.estado() == EstadoPago.RECHAZADA) {
                ac.rechazadas += f.cantidad();
            } else if (f.estado() == EstadoPago.PENDIENTE) {
                ac.pendientes += f.cantidad();
            }
        }

        for (Map.Entry<TipoMetodo, AcumuladoMetodo> entry : mapa.entrySet()) {
            AcumuladoMetodo ac = entry.getValue();
            if (ac.totalTransacciones() > 0) {
                modelo.addRow(new Object[]{
                    entry.getKey(),
                    ac.aprobadas,
                    "$" + ac.montoAprobado,
                    ac.rechazadas,
                    ac.pendientes,
                    ac.totalTransacciones()
                });
            }
        }

        lblTotal.setText("Total transacciones: " + r.totalTransacciones()
                + "   |   Ingresos aprobados (excluye rechazadas): $" + r.totalAprobado());
    }
}
