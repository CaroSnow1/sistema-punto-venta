package mx.unam.dgtic.proyecto_final.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Integer idUsuario;
    private String usuario;
    private String usuRol;
    private Integer usuStatus;
    private String nombreCompleto;
}
