package sistemapagos.cobros;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import sistemadepagos.cobros.CobroPersistenciaException;

/** Panel de pago: selección de venta y método, datos específicos y procesamiento. */
public class CobroPanel extends JPanel {

    private static final String[] METODOS = {"Efectivo", "Tarjeta", "Transferencia bancaria", "Pago electrónico"};

    private final CobroService servicio = new CobroService();

    private final JComboBox<VentaPorCobrar> cmbVenta = new JComboBox<>();
    private final JLabel lblImporte = new JLabel("$0.00");
    private final JComboBox<String> cmbMetodo = new JComboBox<>(METODOS);
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelDatos = new JPanel(cardLayout);

    // Efectivo
    private final JTextField txtRecibido = new JTextField(12);
    // Tarjeta
    private final JTextField txtNumTarjeta = new JTextField(16);
    private final JTextField txtTitular = new JTextField(20);
    private final JTextField txtVencimiento = new JTextField(5);
    private final JPasswordField txtCvv = new JPasswordField(4);
    // Transferencia
    private final JComboBox<String> cmbBanco = new JComboBox<>(new String[]{"Banco A", "Banco B", "Banco C"});
    private final JTextField txtCuenta = new JTextField(20);
    private final JTextField txtReferencia = new JTextField(20);
    // Electrónico
    private final JComboBox<String> cmbPlataforma =
            new JComboBox<>(new String[]{"Billetera digital", "Código QR", "Enlace de pago"});
    private final JTextField txtCorreo = new JTextField(24);

    private final JButton btnProcesar = new JButton("Procesar pago");
    private final JButton btnActualizar = new JButton("Actualizar ventas");

    public CobroPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirDatosMetodo(), BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);

        cmbVenta.addActionListener(e -> mostrarImporte());
        cmbMetodo.addActionListener(e -> cardLayout.show(panelDatos, (String) cmbMetodo.getSelectedItem()));
        btnActualizar.addActionListener(e -> cargarVentas());
        btnProcesar.addActionListener(e -> procesar());

        cargarVentas();
    }

    private JPanel construirEncabezado() {
        JPanel p = new JPanel(new GridLayout(3, 2, 6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Venta y método de pago"));
        p.add(new JLabel("Venta pendiente:"));   p.add(cmbVenta);
        p.add(new JLabel("Importe a pagar:"));   p.add(lblImporte);
        p.add(new JLabel("Método de pago:"));    p.add(cmbMetodo);
        return p;
    }

    private JPanel construirDatosMetodo() {
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos del método seleccionado"));

        panelDatos.add(formulario(new String[]{"Monto recibido ($):"}, new JComponent[]{txtRecibido}), METODOS[0]);
        panelDatos.add(formulario(
                new String[]{"Número de tarjeta (16 dígitos):", "Titular:", "Vencimiento (MM/AA):", "CVV:"},
                new JComponent[]{txtNumTarjeta, txtTitular, txtVencimiento, txtCvv}), METODOS[1]);
        panelDatos.add(formulario(
                new String[]{"Banco:", "Número de cuenta:", "Referencia:"},
                new JComponent[]{cmbBanco, txtCuenta, txtReferencia}), METODOS[2]);
        panelDatos.add(formulario(
                new String[]{"Plataforma:", "Correo de la cuenta:"},
                new JComponent[]{cmbPlataforma, txtCorreo}), METODOS[3]);
        return panelDatos;
    }

    private JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JPanel p = new JPanel(new GridLayout(etiquetas.length, 2, 6, 6));
        for (int i = 0; i < etiquetas.length; i++) {
            p.add(new JLabel(etiquetas[i]));
            p.add(campos[i]);
        }
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(p, BorderLayout.NORTH);
        return envoltorio;
    }

    private JPanel construirBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(btnActualizar);
        p.add(btnProcesar);
        return p;
    }

    // ---------- lógica de interfaz ----------

    private void cargarVentas() {
        try {
            List<VentaPorCobrar> ventas = servicio.listarVentasPendientes();
            cmbVenta.removeAllItems();
            ventas.forEach(cmbVenta::addItem);
            if (ventas.isEmpty()) {
                lblImporte.setText("$0.00");
                JOptionPane.showMessageDialog(this, "No hay ventas pendientes de pago.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }
            mostrarImporte();
        } catch (CobroPersistenciaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarImporte() {
        VentaPorCobrar v = (VentaPorCobrar) cmbVenta.getSelectedItem();
        lblImporte.setText(v == null ? "$0.00" : "$" + v.importe());
    }

    private void procesar() {
        VentaPorCobrar venta = (VentaPorCobrar) cmbVenta.getSelectedItem();
        if (venta == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta pendiente.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            MetodoCobro metodo = crearMetodo();
            ResultadoCobro r = servicio.procesarPago(venta.codigo(), venta.importe(), metodo);
            mostrarResultado(r);
            limpiarCampos();
            cargarVentas();
        } catch (CobroValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (CobroPersistenciaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Construye la subclase según el método elegido (polimorfismo). */
    private MetodoCobro crearMetodo() throws CobroValidacionException {
        switch ((String) cmbMetodo.getSelectedItem()) {
            case "Efectivo":
                return new CobroEfectivo(MetodoCobro.parsearMonto(txtRecibido.getText(), "Monto recibido"));
            case "Tarjeta":
                return new CobroTarjeta(txtNumTarjeta.getText(), txtTitular.getText(),
                        txtVencimiento.getText(), new String(txtCvv.getPassword()));
            case "Transferencia bancaria":
                return new CobroTransferencia((String) cmbBanco.getSelectedItem(),
                        txtCuenta.getText(), txtReferencia.getText());
            case "Pago electrónico":
                return new CobroElectronico((String) cmbPlataforma.getSelectedItem(), txtCorreo.getText());
            default:
                throw new CobroValidacionException("Debe seleccionar un método de pago.");
        }
    }

    private void mostrarResultado(ResultadoCobro r) {
        if (r.aprobado()) {
            String msg = "Pago APROBADO\nTransacción: " + r.idTransaccion();
            if (r.cambio() != null) {
                msg += "\nCambio a entregar: $" + r.cambio();
            }
            JOptionPane.showMessageDialog(this, msg, "Pago aprobado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Pago RECHAZADO\nTransacción: " + r.idTransaccion() + "\nMotivo: " + r.motivo(),
                    "Pago rechazado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtRecibido.setText("");
        txtNumTarjeta.setText("");
        txtTitular.setText("");
        txtVencimiento.setText("");
        txtCvv.setText("");
        txtCuenta.setText("");
        txtReferencia.setText("");
        txtCorreo.setText("");
    }
}