package com.example.reciclaje.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reciclaje.entidades.Rol;
import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.entidades.UsuarioRol;
import com.example.reciclaje.repositorio.RolRepositorio;
import com.example.reciclaje.seguridad.CustomUserDetails;
import com.example.reciclaje.seguridad.SecurityConfig;
import com.example.reciclaje.servicio.UsuarioServicio;
import com.example.reciclaje.servicioDTO.LoginRequest;
import com.example.reciclaje.servicioDTO.UsuarioDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final PasswordEncoder passwordEncoder;
  //  private final SecurityConfig securityConfig;
	 private final UsuarioServicio usuarioService;
	 private final RolRepositorio rolRepository;
	 
	    /**
	     * Registrar un nuevo usuario con el rol USER por defecto.
	     */
	 @PostMapping("/registro")
	    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
	        try {
	            // 1. Buscar el rol por defecto (ej: "USER")
	            Rol rolUser = rolRepository.findByNombre("USER")
	                .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));

	            // 2. Crear la relación UsuarioRol
	            UsuarioRol usuarioRol = new UsuarioRol();
	            usuarioRol.setUsuario(usuario);
	            usuarioRol.setRol(rolUser);
	            usuarioRol.setActivo(true);

	            // 3. Asignar la relación al usuario
	            usuario.getUsuarioRoles().add(usuarioRol);

	            // 4. Guardar el usuario (y cascada guardará UsuarioRol)
	            UsuarioDTO nuevoUsuario = usuarioService.registrarUsuarioConRol(usuario);

	            // 5. Retornar respuesta sin relaciones cíclicas
	            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
	                "id", nuevoUsuario.getId(),
	                "email", nuevoUsuario.getEmail(),
	                "mensaje", "Usuario registrado exitosamente"
	            ));
	        } catch (IllegalArgumentException ex) {
	            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	        }
	    }

	    /**
	     * Obtener el perfil de un usuario por su ID.
	     */
	    @GetMapping("/{id:\\d+}")
	    public ResponseEntity<Usuario> obtenerPerfilPorId(@PathVariable Long id) {
	        Usuario usuario = usuarioService.obtenerPerfil(id);
	        return ResponseEntity.ok(usuario);
	    }

	    /**
	     * Obtener el perfil del usuario autenticado desde la sesión.
	     */
	    @GetMapping("/perfil")
	    public ResponseEntity<Usuario> obtenerPerfilDesdeSesion(Authentication authentication) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(401).build(); // No autenticado
	        }

	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        Usuario usuario = usuarioService.obtenerPerfilPorEmail(userDetails.getUsername());
	        return ResponseEntity.ok(usuario);
	    }
	}
