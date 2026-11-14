package sistemapagos.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import sistemapagos.model.Venta;
import sistemapagos.service.VentaService;

/**
 * Módulo de ventas (Proceso 1). Solo recoge datos, llama a VentaService y muestra el resultado.
 * No contiene SQL ni reglas de negocio.
 */
public class VentasPanel extends JPanel {

    private final VentaService ventaService;

    // Formulario
    private final JTextField txtCodigo = new JTextField(12);
    private final JTextField txtDescripcion = new JTextField(25);
    private final JTextField txtImporte = new JTextField(10);
    private final JTextField txtFecha = new JTextField(10);
    private final JButton btnRegistrar = new JButton("Registrar");
    private final JButton btnModificar = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    // Búsqueda
    private final JComboBox<String> cmbCriterio = new JComboBox<>(new String[]{"Código", "Fecha"});
    private final JTextField txtBusqueda = new JTextField(12);
    private final JLabel lblHasta = new JLabel("hasta (opcional):");
    private final JTextField txtHasta = new JTextField(10);
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnTodas = new JButton("Mostrar todas");
    private final JButton btnDetalle = new JButton("Ver detalle");

    // Tabla
    private final DefaultTableModel modelo =
            UiUtil.modeloTabla("Código", "Descripción", "Importe", "Fecha", "Estado");
    private final JTable tabla = new JTable(modelo);

    private List<Venta> mostradas = new ArrayList<>();
    private Venta seleccionada;   // null = modo alta

    public VentasPanel(VentaService ventaService) {
        this.ventaService = ventaService;
        construirInterfaz();
        registrarEventos();
        actualizarCriterio();
        limpiarFormulario();
        cargarTodas();
    }

    // ------------------------------------------------------------------
    // Interfaz
    // ------------------------------------------------------------------

    private void construirInterfaz() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.add(construirFormulario(), BorderLayout.NORTH);
        norte.add(construirBusqueda(), BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Datos de la venta"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Código:"), c);
        c.gridx = 1; p.add(txtCodigo, c);
        c.gridx = 2; p.add(new JLabel("Descripción:"), c);
        c.gridx = 3; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; p.add(txtDescripcion, c);

        c.fill = GridBagConstraints.NONE; c.weightx = 0;
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Importe:"), c);
        c.gridx = 1; p.add(txtImporte, c);
        c.gridx = 2; p.add(new JLabel("Fecha (AAAA-MM-DD):"), c);
        c.gridx = 3; p.add(txtFecha, c);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnRegistrar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 4; p.add(botones, c);
        return p;
    }

    private JPanel construirBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p.setBorder(BorderFactory.createTitledBorder("Buscar ventas"));
        p.add(new JLabel("Buscar por:"));
        p.add(cmbCriterio);
        p.add(txtBusqueda);
        p.add(lblHasta);
        p.add(txtHasta);
        p.add(btnBuscar);
        p.add(btnTodas);
        p.add(btnDetalle);
        return p;
    }

    private void registrarEventos() {
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> buscar());
        btnTodas.addActionListener(e -> cargarTodas());
        btnDetalle.addActionListener(e -> verDetalle());
        cmbCriterio.addActionListener(e -> actualizarCriterio());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    private void registrar() {
        UiUtil.ejecutar(this, () -> {
            Venta v = ventaService.registrar(txtCodigo.getText(), txtDescripcion.getText(),
                    txtImporte.getText(), txtFecha.getText());
            UiUtil.info(this, "Venta " + v.getCodigo() + " registrada correctamente.");
            cargarTodas();
        });
    }

    private void modificar() {
        if (seleccionada == null) {
            UiUtil.info(this, "Seleccione una venta de la tabla para modificarla.");
            return;
        }
        UiUtil.ejecutar(this, () -> {
            Venta v = ventaService.modificar(seleccionada.getId(), txtCodigo.getText(),
                    txtDescripcion.getText(), txtImporte.getText(), txtFecha.getText());
            UiUtil.info(this, "Venta " + v.getCodigo() + " modificada correctamente.");
            cargarTodas();
        });
    }

    private void eliminar() {
        if (seleccionada == null) {
            UiUtil.info(this, "Seleccione una venta de la tabla para eliminarla.");
            return;
        }
        if (!UiUtil.confirmar(this, "¿Desea eliminar la venta " + seleccionada.getCodigo() + "?")) {
            return;
        }
        UiUtil.ejecutar(this, () -> {
            ventaService.eliminar(seleccionada.getId());
            UiUtil.info(this, "Venta eliminada correctamente.");
            cargarTodas();
        });
    }

    private void buscar() {
        UiUtil.ejecutar(this, () -> {
            List<Venta> resultado;
            if (cmbCriterio.getSelectedIndex() == 0) {
                resultado = Collections.singletonList(ventaService.buscarPorCodigo(txtBusqueda.getText()));
            } else {
                resultado = ventaService.buscarPorFecha(txtBusqueda.getText(), txtHasta.getText());
            }
            mostrar(resultado);
            if (resultado.isEmpty()) {
                UiUtil.info(this, "No se encontraron ventas en esa fecha.");
            }
        });
    }

    private void verDetalle() {
        if (seleccionada == null) {
            UiUtil.info(this, "Seleccione una venta de la tabla para ver su detalle.");
            return;
        }
        UiUtil.ejecutar(this, () -> {
            Venta v = ventaService.obtener(seleccionada.getId());
            UiUtil.info(this, "Código: " + v.getCodigo()
                    + "\nDescripción: " + v.getDescripcion()
                    + "\nImporte: $" + v.getImporte().toPlainString()
                    + "\nFecha: " + v.getFecha()
                    + "\nEstado: " + v.getEstado());
        });
    }

    // ------------------------------------------------------------------
    // Utilidades de la vista
    // ------------------------------------------------------------------

    private void cargarTodas() {
        UiUtil.ejecutar(this, () -> mostrar(ventaService.listar()));
    }

    /** Llena la tabla y deja el formulario en modo alta. */
    private void mostrar(List<Venta> ventas) {
        limpiarFormulario();
        mostradas = new ArrayList<>(ventas);
        modelo.setRowCount(0);
        for (Venta v : mostradas) {
            modelo.addRow(new Object[]{v.getCodigo(), v.getDescripcion(),
                    v.getImporte().toPlainString(), v.getFecha(), v.getEstado()});
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= mostradas.size()) {
            return;
        }
        seleccionada = mostradas.get(fila);
        txtCodigo.setText(seleccionada.getCodigo());
        txtDescripcion.setText(seleccionada.getDescripcion());
        txtImporte.setText(seleccionada.getImporte().toPlainString());
        txtFecha.setText(seleccionada.getFecha().toString());

        boolean editable = seleccionada.puedeModificarse();
        txtCodigo.setEditable(editable);
        txtDescripcion.setEditable(editable);
        txtImporte.setEditable(editable);
        txtFecha.setEditable(editable);
        btnRegistrar.setEnabled(false);
        btnModificar.setEnabled(editable);
        btnEliminar.setEnabled(editable);
    }

    private void limpiarFormulario() {
        seleccionada = null;
        tabla.clearSelection();
        txtCodigo.setText("");
        txtDescripcion.setText("");
        txtImporte.setText("");
        txtFecha.setText(LocalDate.now().toString());

        txtCodigo.setEditable(true);
        txtDescripcion.setEditable(true);
        txtImporte.setEditable(true);
        txtFecha.setEditable(true);
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    /** "hasta" solo aplica cuando se busca por fecha. */
    private void actualizarCriterio() {
        boolean porFecha = cmbCriterio.getSelectedIndex() == 1;
        lblHasta.setVisible(porFecha);
        txtHasta.setVisible(porFecha);
        txtBusqueda.setToolTipText(porFecha ? "AAAA-MM-DD" : "Código de la venta");
    }
}