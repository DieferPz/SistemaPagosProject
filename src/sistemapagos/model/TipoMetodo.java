package sistemapagos.model;

public enum TipoMetodo {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia bancaria"),
    PAGO_ELECTRONICO("Pago electrónico");

    private final String etiqueta;

    TipoMetodo(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    /** Se usa para que un JComboBox muestre el texto amigable. */
    @Override
    public String toString() { return etiqueta; }
}
