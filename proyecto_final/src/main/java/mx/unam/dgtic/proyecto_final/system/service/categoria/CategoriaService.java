package mx.unam.dgtic.proyecto_final.system.service.categoria;

import mx.unam.dgtic.proyecto_final.system.model.entities.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface   CategoriaService {
    Page<Categoria> buscarCategoria(Pageable pageable);
    List<Categoria> buscarCategoria();
    void guardar(Categoria categoria);
    void borrar(Integer id);
    Categoria buscarCategoriaId(Integer id);
}
