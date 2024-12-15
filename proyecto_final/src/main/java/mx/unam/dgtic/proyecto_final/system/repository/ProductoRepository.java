package mx.unam.dgtic.proyecto_final.system.repository;

import jakarta.transaction.Transactional;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    //Obtener todos los productos activos
    Page<Producto> findByProdStatus(String prodStatus, Pageable pageable);

    // Método para buscar productos por categoría
    //Page<Producto> findByCategoria_IdCategoria(Long categoriaId, Pageable pageable);
    Page<Producto> findByCategoriaIdCategoriaAndProdStatus(Long idCategoria, String prodStatus, Pageable pageable);

    //Contar productos activos
    long countByProdStatus(String prodStatus);

    // Contar productos por categoria
    long countByCategoriaIdCategoriaAndProdStatus(Long idCategoria, String prodStatus);


    //Devolver los productos en una lista
    List<Producto> findByCategoria_IdCategoria(Long categoriaId);

    @Modifying
    @Query("UPDATE Producto p SET p.categoria.idCategoria = 1 WHERE p.categoria.idCategoria = :idCategoria")
    int reasignarProductosASinCategoria(Integer idCategoria);

    //REPORTES
    @Query("SELECT p FROM Producto p WHERE p.stock <= 10 AND p.prodStatus = 'ACTIVO'")
    Page<Producto> findProductosConExistenciasBajas(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.stock = 0 AND p.prodStatus = 'ACTIVO'")
    Page<Producto> findProductosAgotados(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= 10 AND p.prodStatus = 'ACTIVO'")
    long contarProductosConExistenciasBajas();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock = 0 AND p.prodStatus = 'ACTIVO'")
    long contarProductosAgotados();

    // Contar productos con existencias bajas por categoría
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= 10 AND p.prodStatus = 'ACTIVO' AND p.categoria.idCategoria = :categoriaId")
    long contarProductosConExistenciasBajasPorCategoria(@Param("categoriaId") Long categoriaId);

    // Contar productos agotados por categoría
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock = 0 AND p.prodStatus = 'ACTIVO' AND p.categoria.idCategoria = :categoriaId")
    long contarProductosAgotadosPorCategoria(@Param("categoriaId") Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.stock <= 10 AND p.prodStatus = 'ACTIVO' AND p.categoria.idCategoria = :categoriaId")
    Page<Producto> findProductosConExistenciasBajasPorCategoria(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.stock = 0 AND p.prodStatus = 'ACTIVO' AND p.categoria.idCategoria = :categoriaId")
    Page<Producto> findProductosAgotadosPorCategoria(@Param("categoriaId") Long categoriaId, Pageable pageable);


}
