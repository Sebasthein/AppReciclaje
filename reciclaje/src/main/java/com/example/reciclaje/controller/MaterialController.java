package com.example.reciclaje.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.reciclaje.entidades.Material;
import com.example.reciclaje.repositorio.MaterialRepositorio;
import com.example.reciclaje.servicio.MaterialServicio;
import org.springframework.http.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
public class MaterialController {

	private final MaterialServicio materialService;

	// Constructor explícito eliminado gracias a @RequiredArgsConstructor

	@GetMapping
	public ResponseEntity<List<Material>> listarMateriales() {
		return ResponseEntity.ok(materialService.obtenerTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Material> obtenerMaterial(@PathVariable Long id) {
		return materialService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/tipo/{tipo}")
	public ResponseEntity<List<Material>> buscarPorTipo(@PathVariable String tipo) {
		return ResponseEntity.ok(materialService.buscarPorTipo(tipo));
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Material> crearMaterial(@RequestBody Material material) {
		return ResponseEntity.status(HttpStatus.CREATED).body(materialService.crearMaterial(material));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Material> actualizarMaterial(@PathVariable Long id, @RequestBody Material material) {
		return ResponseEntity.ok(materialService.actualizarMaterial(id, material));
	}

	@PostMapping("/crear-desde-qr")
	public ResponseEntity<Map<String, Object>> crearMaterialDesdeQR( // Mejor tipado para la respuesta
			@RequestBody Map<String, String> request, @AuthenticationPrincipal UserDetails userDetails) { // userDetails
																											// no se usa
																											// directamente
																											// aquí

		try {
			String qrData = request.get("qrData");
			Material material = materialService.crearMaterialDesdeQR(qrData);

			return ResponseEntity
					.ok(Map.of("success", true, "message", "Material creado exitosamente", "material", material));

		} catch (IllegalArgumentException e) { // Capturar excepciones más específicas si es posible
			return ResponseEntity.badRequest()
					.body(Map.of("success", false, "error", "Datos de QR inválidos", "message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of( // Usar INTERNAL_SERVER_ERROR
																						// para errores inesperados
					"success", false, "error", "Error interno del servidor", "message", e.getMessage()));
		}
	}

	@PostMapping("/buscar-por-codigo")
	public ResponseEntity<?> buscarPorCodigo(@RequestBody Map<String, Object> codigoRequest) {
		try {
			// Extraer los datos del mapa
			String codigoBarra = (String) codigoRequest.get("codigo_barra");
			// ... otros campos que necesites

			// Lógica de búsqueda
			Material material = materialService.buscarPorCodigo(codigoBarra);
			return ResponseEntity.ok(material);
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "Error al buscar material", "details", e.getMessage()));
		}
	}
}
