package com.example.reciclaje.controller;

import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.servicio.UsuarioServicio;
import com.example.reciclaje.servicioDTO.LoginRequest;
import com.example.reciclaje.servicioDTO.UsuarioDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {

	private final UsuarioServicio usuarioServicio;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public AuthController(UsuarioServicio usuarioServicio, AuthenticationManager authenticationManager) {
		this.usuarioServicio = usuarioServicio;
		this.authenticationManager = authenticationManager;
	}
	

	// 👉 Mostrar formulario de login
	@GetMapping("/login")
	public String mostrarLogin(Model model) {
		model.addAttribute("loginRequest", new LoginRequest());
		return "login";
	}

	// 👉 Procesar inicio de sesión
	@PostMapping("/login")
	public String procesarLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest, Model model) {
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(auth);
			return "redirect:/dashboard";
		} catch (Exception e) {
			model.addAttribute("error", "Credenciales inválidas");
			return "login";
		}
	}

	// 👉 Mostrar formulario de registro
	@GetMapping("/registro")
	public String mostrarFormularioRegistro(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "registro";
	}

	// 👉 Procesar registro de usuario
	 @PostMapping("/registro")
	    public ResponseEntity<Map<String, Object>> registrarUsuario(@RequestBody Usuario usuario) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            UsuarioDTO nuevoUsuario = usuarioServicio.registrarUsuarioConRol(usuario);
	            response.put("success", true);
	            response.put("usuario", nuevoUsuario);
	            return ResponseEntity.status(HttpStatus.CREATED).body(response);
	        } catch (IllegalArgumentException ex) {
	            response.put("success", false);
	            response.put("error", ex.getMessage());
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }
	    }
	

	// 👉 Página principal después del login
	@GetMapping("/dashboard")
	public String dashboard(Authentication authentication, Model model) {
		model.addAttribute("email", authentication.getName());
		model.addAttribute("points", 1250); // Aquí puedes implementar la lógica para obtener los puntos reales
		return "dashboard";
	}

	// 👉 Página de acceso denegado
	@GetMapping("/access-denied")
	public String accesoDenegado() {
		return "acceso-denegado";
	}

}
