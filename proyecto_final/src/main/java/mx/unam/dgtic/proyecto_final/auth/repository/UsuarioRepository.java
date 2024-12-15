package mx.unam.dgtic.proyecto_final.auth.repository;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Page<Usuario> findByUsuRol(String usuRol, Pageable pageable);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.usuRol = :rol AND u.usuStatus = :status AND u.username != :username")
    boolean existsByUsuRolAndUsuStatus(@Param("rol") String rol, @Param("status") int status, @Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.usuRol = 'CAJERO' AND u.usuStatus = 1 AND u.idUsuario != :idUsuario")
    boolean existsByCajeroActivoExcept(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT u FROM Usuario u WHERE u.usuRol = :rol")
    List<Usuario> findUsuariosByRol(@Param("rol") String rol);



}
