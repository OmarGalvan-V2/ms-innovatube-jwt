package com.capysoft.innovatube_jwt.controller;

import com.capysoft.innovatube_jwt.dto.JwtRequest;
import com.capysoft.innovatube_jwt.dto.JwtResponse;
import com.capysoft.innovatube_jwt.service.JwtService;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt")
public class JwtController {

    Logger log = LoggerFactory.getLogger(JwtController.class);

    @Autowired
    private JwtService jwtService;

    @Value("${configuracion.texto}")
	private String texto;

    @Autowired
	private Environment env; 

	@GetMapping("/texto-config")
	public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String puerto) {
		Map<String, String> response = new HashMap<>();
		response.put("texto", texto);
		response.put("puerto", puerto);
		log.info("Texto: " + texto + " Puerto: " + puerto);
		if(env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")){
			response.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
			response.put("autor.email", env.getProperty("configuracion.autor.email"));
		}
		return ResponseEntity.ok().body(response);
	}

    @PostMapping("/generar")
    public ResponseEntity<JwtResponse> generarToken(@RequestBody JwtRequest solicitud) {
        String token = jwtService.generarToken(solicitud.getUsuario(), solicitud.getIdUsuario());
        return ResponseEntity.ok(new JwtResponse(token, solicitud.getUsuario(), solicitud.getIdUsuario(), solicitud.getCorreo()));
    }

    @PostMapping("/validar")
    public ResponseEntity<Boolean> validarToken(@RequestParam String token, @RequestParam String nombreUsuario) {
        Boolean esValido = jwtService.validarToken(token, nombreUsuario);
        return ResponseEntity.ok(esValido);
    }

    @GetMapping("/extraer-usuario")
    public ResponseEntity<String> extraerNombreUsuario(@RequestParam String token) {
        String nombreUsuario = jwtService.extraerNombreUsuario(token);
        return ResponseEntity.ok(nombreUsuario);
    }

    @GetMapping("/extraer-id-usuario")
    public ResponseEntity<Long> extraerIdUsuario(@RequestParam String token) {
        Long idUsuario = jwtService.extraerIdUsuario(token);
        return ResponseEntity.ok(idUsuario);
    }
}