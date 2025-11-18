package com.capysoft.innovatube_jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String usuario;
    private Long idUsuario;
    private String correo;
}