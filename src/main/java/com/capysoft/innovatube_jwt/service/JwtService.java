package com.capysoft.innovatube_jwt.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey obtenerClaveSecreta() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String usuario, Long idUsuario) {
        Map<String, Object> reclamaciones = new HashMap<>();
        reclamaciones.put("idUsuario", idUsuario);
        return crearToken(reclamaciones, usuario);
    }

    private String crearToken(Map<String, Object> reclamaciones, String sujeto) {
        return Jwts.builder()
                .claims(reclamaciones)
                .subject(sujeto)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(obtenerClaveSecreta())
                .compact();
    }

    public Boolean validarToken(String token, String nombreUsuario) {
        final String usuarioExtraido = extraerNombreUsuario(token);
        return (usuarioExtraido.equals(nombreUsuario) && !estaTokenExpirado(token));
    }

    public String extraerNombreUsuario(String token) {
        return extraerReclamacion(token, Claims::getSubject);
    }

    public Long extraerIdUsuario(String token) {
        return extraerReclamacion(token, reclamaciones -> reclamaciones.get("idUsuario", Long.class));
    }

    public Date extraerFechaExpiracion(String token) {
        return extraerReclamacion(token, Claims::getExpiration);
    }

    public <T> T extraerReclamacion(String token, java.util.function.Function<Claims, T> resolvedor) {
        final Claims reclamaciones = extraerTodasLasReclamaciones(token);
        return resolvedor.apply(reclamaciones);
    }

    private Claims extraerTodasLasReclamaciones(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveSecreta())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean estaTokenExpirado(String token) {
        return extraerFechaExpiracion(token).before(new Date());
    }
}