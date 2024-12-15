package mx.unam.dgtic.proyecto_final.auth.service;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Page<Usuario> buscarUsuario(Pageable pageable);
    List<Usuario> buscarUsuario();
    void guardar(Usuario usuario);
    void darDeBaja(Integer id);
    Usuario buscarUsuarioId(Integer id);
    Usuario obtenerPorUsuario(String usuario);
    Page<Usuario> buscarUsuarioPorRol(String rol, Pageable pageable);

    boolean validarCredenciales(String username, String password, String rol);

    boolean existeOtroCajeroActivo(Integer idUsuario);

    List<Usuario> obtenerUsuariosPorRol(String rol);
}
