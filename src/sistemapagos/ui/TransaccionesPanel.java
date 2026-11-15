package sistemapagos.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sistemapagos.exception.ValidacionException;
import sistemapagos.model.EstadoPago;
import sistemapagos.model.TipoMetodo;
import sistemapagos.model.Transaccion;
import sistemapagos.service.ConsultaService;

/**
 * BILLY - Proceso: consulta de transacciones.
 */
public class TransaccionesPanel extends JPanel {
    private final ConsultaService consultaService;
    private final DefaultTableModel modelo = UiUtil.modeloTabla(
            "Código", "Venta", "Método", "Importe", "Fecha", "Estado", "Motivo", "Detalle");
    private final JTable tabla = new JTable(modelo);

    // Controles de búsqueda y filtros
    private final JTextField txtCodigo = new JTextField(12);
    private final JComboBox<Object> cmbMetodo = new JComboBox<>();
    private final JComboBox<Object> cmbEstado = new JComboBox<>();
    private final JTextField txtDesde = new JTextField(10);
    private final JTextField txtHasta = new JTextField(10);
    private final JTextField txtVentaId = new JTextField(6);
    private final JLabel lblInfo = new JLabel(" ");

    public TransaccionesPanel(ConsultaService consultaService) {
        this.consultaService = consultaService;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inicializarCombos();

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Búsqueda y filtros de transacciones"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 5, 4, 5);
        g.anchor = GridBagConstraints.WEST;

        // Fila 0: Búsqueda exacta por código (4.1)
        g.gridx = 0; g.gridy = 0;
        panelSuperior.add(new JLabel("Código transacción:"), g);
        g.gridx = 1;
        panelSuperior.add(txtCodigo, g);

        JButton btnBuscarCodigo = new JButton("Buscar código");
        btnBuscarCodigo.addActionListener(e -> UiUtil.ejecutar(this, this::buscarPorCodigo));
        g.gridx = 2;
        panelSuperior.add(btnBuscarCodigo, g);

        g.gridx = 3;
        panelSuperior.add(new JLabel("ID Venta:"), g);
        g.gridx = 4;
        panelSuperior.add(txtVentaId, g);

        // Fila 1: Filtros combinados (4.2, 4.3, 4.4)
        g.gridx = 0; g.gridy = 1;
        panelSuperior.add(new JLabel("Método:"), g);
        g.gridx = 1;
        panelSuperior.add(cmbMetodo, g);

        g.gridx = 2;
        panelSuperior.add(new JLabel("Estado:"), g);
        g.gridx = 3;
        panelSuperior.add(cmbEstado, g);

        // Fila 2: Fechas y botones de acción
        g.gridx = 0; g.gridy = 2;
        panelSuperior.add(new JLabel("Desde (AAAA-MM-DD):"), g);
        g.gridx = 1;
        panelSuperior.add(txtDesde, g);

        g.gridx = 2;
        panelSuperior.add(new JLabel("Hasta (AAAA-MM-DD):"), g);
        g.gridx = 3;
        panelSuperior.add(txtHasta, g);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> UiUtil.ejecutar(this, this::aplicarFiltros));

        JButton btnLimpiar = new JButton("Limpiar / Ver todas");
        btnLimpiar.addActionListener(e -> UiUtil.ejecutar(this, this::limpiarYCargar));

        botones.add(btnFiltrar);
        botones.add(btnLimpiar);

        g.gridx = 4; g.gridwidth = 2;
        panelSuperior.add(botones, g);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);

        UiUtil.ejecutar(this, this::cargarTodas);
    }

    private void inicializarCombos() {
        cmbMetodo.addItem("Todos");
        for (TipoMetodo m : TipoMetodo.values()) {
            cmbMetodo.addItem(m);
        }

        cmbEstado.addItem("Todos");
        for (EstadoPago ep : EstadoPago.values()) {
            cmbEstado.addItem(ep);
        }
    }

    private void cargarTodas() {
        llenarTabla(consultaService.listarTodas());
    }

    private void buscarPorCodigo() {
        Transaccion t = consultaService.buscarPorCodigo(txtCodigo.getText());
        llenarTabla(List.of(t));
    }

    private void aplicarFiltros() {
        TipoMetodo metodo = (cmbMetodo.getSelectedItem() instanceof TipoMetodo m) ? m : null;
        EstadoPago estado = (cmbEstado.getSelectedItem() instanceof EstadoPago ep) ? ep : null;
        LocalDate desde = parsearFecha(txtDesde.getText(), "Fecha desde");
        LocalDate hasta = parsearFecha(txtHasta.getText(), "Fecha hasta");
        Integer ventaId = parsearEnteroOpcional(txtVentaId.getText(), "ID Venta");

        List<Transaccion> resultados = consultaService.filtrar(metodo, estado, desde, hasta, ventaId);
        llenarTabla(resultados);

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron transacciones con los filtros seleccionados.",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limpiarYCargar() {
        txtCodigo.setText("");
        txtVentaId.setText("");
        txtDesde.setText("");
        txtHasta.setText("");
        cmbMetodo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cargarTodas();
    }

    private void llenarTabla(List<Transaccion> lista) {
        modelo.setRowCount(0);
        for (Transaccion t : lista) {
            modelo.addRow(new Object[]{
                t.getCodigo(), t.getVentaCodigo(), t.getMetodo(), t.getImporte(),
                t.getFecha(), t.getEstado(), t.getMotivo(), t.getDetalle()
            });
        }
        lblInfo.setText(lista.isEmpty()
                ? "No hay transacciones registradas que coincidan con la búsqueda."
                : "Mostrando " + lista.size() + " transacción(es).");
    }

    private LocalDate parsearFecha(String texto, String campo) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) return null;
        try {
            return LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new ValidacionException(campo + " inválida. Use el formato AAAA-MM-DD (ejemplo: 2026-11-15).");
        }
    }

    private Integer parsearEnteroOpcional(String texto, String campo) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) return null;
        try {
            int val = Integer.parseInt(limpio);
            if (val <= 0) throw new ValidacionException(campo + " debe ser un número positivo.");
            return val;
        } catch (NumberFormatException e) {
            throw new ValidacionException(campo + " debe ser un número entero válido.");
        }
    }
}
