package mx.unam.dgtic.proyecto_final.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private String usuario;
    private String usuPassword;
    private String usuRol;
    private String nombre;
    private String apPaterno;
    private String apMaterno;
    private String genero;
    private String horarioEntrada;
    private String horarioSalida;

}

