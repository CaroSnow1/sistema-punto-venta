package mx.unam.dgtic.proyecto_final.security.model;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String username;
    private String fullName;
    private Collection<? extends GrantedAuthority> authorities;
    private Usuario usuario;


    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
        this.id = usuario.getIdUsuario();
        this.username = usuario.getUsername();
        this.fullName = String.format("%s %s %s",
                usuario.getNombre(), usuario.getApPaterno(), usuario.getApMaterno());

        //Necesitan iniciar con ROLE_
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getUsuRol().toUpperCase()));
    }

    public static UserDetailsImpl build(Usuario usuario) {
        return new UserDetailsImpl(usuario);
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getUsuPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getUsuStatus() != 2;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getUsuStatus() == 1;
    }
}
