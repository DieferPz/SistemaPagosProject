package sistemapagos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServicioPagos {
    private final RepositorioTransacciones repositorio;
    private final List<Venta> ventas = new ArrayList<>();

    public ServicioPagos(RepositorioTransacciones repositorio) {
        this.repositorio = repositorio;
    }

    public Venta registrarVenta(Map<String, Double> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un item.");
        }
        Venta venta = new Venta(items);
        ventas.add(venta);
        return venta;
    }

    public Transaccion procesarPago(Venta venta, MetodoPago metodo) {
        if (venta.isPagada()) {
            throw new IllegalStateException("La venta " + venta.getId() + " ya fue pagada.");
        }
        Transaccion transaccion = metodo.procesar(venta);
        repositorio.guardar(transaccion);
        if (transaccion.getEstado() == EstadoPago.APROBADO) {
            venta.setPagada(true);
        }
        return transaccion;
    }

    public List<Transaccion> consultarTransacciones() {
        return repositorio.listar();
    }

    public List<Venta> listarVentas() {
        return Collections.unmodifiableList(ventas);
    }

    public ResumenPagos generarResumen() {
        ResumenPagos resumen = new ResumenPagos();
        for (Transaccion t : repositorio.listar()) {
            resumen.agregar(t);
        }
        return resumen;
    }
}
