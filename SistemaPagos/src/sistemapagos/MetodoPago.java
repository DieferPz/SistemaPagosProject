package sistemapagos;

public interface MetodoPago {
    String getNombre();
    Transaccion procesar(Venta venta);
    boolean validar(Transaccion transaccion);
}
