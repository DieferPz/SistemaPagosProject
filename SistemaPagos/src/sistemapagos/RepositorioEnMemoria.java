package sistemapagos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepositorioEnMemoria implements RepositorioTransacciones {
    private final List<Transaccion> transacciones = new ArrayList<>();

    @Override
    public void guardar(Transaccion transaccion) {
        transacciones.add(transaccion);
    }

    @Override
    public List<Transaccion> listar() {
        return Collections.unmodifiableList(new ArrayList<>(transacciones));
    }

    @Override
    public Transaccion buscarPorId(String id) {
        for (Transaccion t : transacciones) {
            if (t.getId().equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }
}
