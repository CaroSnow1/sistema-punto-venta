package mx.unam.dgtic.proyecto_final.system.repository;

import mx.unam.dgtic.proyecto_final.system.model.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

}
