package mx.unam.dgtic.proyecto_final.system.service.categoria;

import mx.unam.dgtic.proyecto_final.system.model.entities.Categoria;
import mx.unam.dgtic.proyecto_final.system.repository.CategoriaRepository;
import mx.unam.dgtic.proyecto_final.system.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService{

    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    ProductoRepository productoRepository;

    @Override
    public Page<Categoria> buscarCategoria(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    @Override
    public List<Categoria> buscarCategoria() {
        return categoriaRepository.findAll();
    }

    @Override
    public void guardar(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Transactional
    @Override
    public void borrar(Integer id) {
        // Verificar si existe la categoría
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La categoría con ID " + id + " no existe."));

        // Reasignar productos a la categoría genérica ('Sin categoría')
        productoRepository.reasignarProductosASinCategoria(id);

        // Eliminar la categoría
        categoriaRepository.deleteById(id);
    }

    @Override
    public Categoria buscarCategoriaId(Integer id) {
        Optional<Categoria> op = categoriaRepository.findById(id);
        return op.orElse(null);
    }

}
