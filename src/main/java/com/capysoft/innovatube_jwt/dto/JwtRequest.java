package com.capysoft.innovatube_jwt.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String usuario;
    private Long idUsuario;
    private String correo;
}