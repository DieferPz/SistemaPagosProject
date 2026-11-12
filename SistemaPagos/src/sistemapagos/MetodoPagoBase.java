package sistemapagos;

public abstract class MetodoPagoBase implements MetodoPago {
    protected final String nombre;
    protected final double comision;

    protected MetodoPagoBase(String nombre, double comision) {
        this.nombre = nombre;
        this.comision = comision;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    // Metodo plantilla: fija el orden de los pasos y no se puede sobrescribir.
    @Override
    public final Transaccion procesar(Venta venta) {
        double monto = Math.round(venta.getTotal() * (1 + comision) * 100.0) / 100.0;

        boolean autorizado = autorizar(monto);               // paso variable 1
        Transaccion transaccion = new Transaccion(venta, nombre, monto);
        boolean valido = validar(transaccion);               // paso variable 2

        transaccion.setEstado(autorizado && valido ? EstadoPago.APROBADO : EstadoPago.RECHAZADO); //Seteamos estado para todas las clases que hereden de esta clase Abstracta. Si el pago es autorizado (Mayor al monto) y valido (pago mayor a cero)
        return transaccion;
    }

    protected abstract boolean autorizar(double monto);

    @Override
    public abstract boolean validar(Transaccion transaccion);
}
