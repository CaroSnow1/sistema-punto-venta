package mx.unam.dgtic.proyecto_final.auth.service.impl;

import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Page<Usuario> buscarUsuario(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public List<Usuario> buscarUsuario() {
        return usuarioRepository.findAll();
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public void darDeBaja(Integer id) {
        // Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + id + " no existe."));

        // Cambiar el estatus a 2 : BAJA
        usuario.setUsuStatus(2);

        // Guardar los cambios
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarUsuarioId(Integer id) {
        Optional<Usuario> op = usuarioRepository.findById(id);
        return op.orElse(null);
    }

    @Override
    public Usuario obtenerPorUsuario(String usuario) {
        Optional<Usuario> op = usuarioRepository.findByUsername(usuario);
        return op.orElse(null);
    }

    @Override
    public Page<Usuario> buscarUsuarioPorRol(String rol, Pageable pageable) {
        return usuarioRepository.findByUsuRol(rol, pageable);
    }

    @Override
    public boolean validarCredenciales(String username, String password, String rol) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario != null && usuario.getUsuRol().equalsIgnoreCase(rol)) {
            return passwordEncoder.matches(password, usuario.getUsuPassword());
        }
        return false;
    }

    @Override
    public boolean existeOtroCajeroActivo(Integer idUsuario) {
        return usuarioRepository.existsByCajeroActivoExcept(idUsuario);
    }

    @Override
    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        return usuarioRepository.findUsuariosByRol(rol);
    }


}
