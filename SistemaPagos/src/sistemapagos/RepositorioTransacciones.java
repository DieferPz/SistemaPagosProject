package sistemapagos;

import java.util.List;

public interface RepositorioTransacciones {
    void guardar(Transaccion transaccion);
    List<Transaccion> listar();
    Transaccion buscarPorId(String id);
}
