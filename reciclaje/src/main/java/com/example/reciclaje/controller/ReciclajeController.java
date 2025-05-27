package com.example.reciclaje.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
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

import com.example.reciclaje.entidades.Reciclaje;
import com.example.reciclaje.seguridad.CustomUserDetails;
import com.example.reciclaje.servicio.ReciclajeServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reciclajes")
@RequiredArgsConstructor
public class ReciclajeController {

	  private final ReciclajeServicio reciclajeService;

	    /**
	     * Registrar una solicitud de reciclaje desde un usuario autenticado.
	     * @param datos Contiene materialId y fotoUrl.
	     */
	  @PostMapping("/registrar")
	  public ResponseEntity<?> registrarReciclaje(
	      @RequestParam Long usuarioId,
	      @RequestParam Long materialId,
	      @RequestParam int cantidad) {
	      
	      try {
	          Reciclaje reciclaje = reciclajeService.registrarReciclaje(usuarioId, materialId, cantidad);
	          return ResponseEntity.status(HttpStatus.CREATED).body(reciclaje);
	      } catch (Exception e) {
	          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	              .body("Error al registrar el reciclaje: " + e.getMessage());
	      }
	  }

	    /**
	     * Obtener reciclajes hechos por un usuario específico.
	     */
	    @GetMapping("/usuario/{usuarioId}")
	    public ResponseEntity<?> reciclajesPorUsuario(@PathVariable Long usuarioId) {
	        List<Reciclaje> lista = reciclajeService.obtenerReciclajesPorUsuario(usuarioId);

	        if (lista.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontraron reciclajes para el usuario con ID " + usuarioId);
	        }

	        return ResponseEntity.ok(lista);
	    }

	    /**
	     * Validar un reciclaje pendiente (por un moderador/admin).
	     */
	    @PutMapping("/validar/{id}")
	    public ResponseEntity<?> validar(@PathVariable Long id) {
	        try {
	            Reciclaje reciclajeValidado = reciclajeService.validarReciclaje(id);
	            return ResponseEntity.ok(reciclajeValidado);
	        } catch (IllegalArgumentException ex) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Reciclaje no encontrado o no válido: " + ex.getMessage());
	        }
	    }
    
  
}
