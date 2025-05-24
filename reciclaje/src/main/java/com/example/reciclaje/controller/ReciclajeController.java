package com.example.reciclaje.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.reciclaje.entidades.Reciclaje;
import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.repositorio.MaterialRepositorio;
import com.example.reciclaje.repositorio.UsuarioRepositorio;
import com.example.reciclaje.seguridad.CustomUserDetails;
import com.example.reciclaje.servicio.ReciclajeServicio;
import com.example.reciclaje.servicioDTO.MaterialScanResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reciclajes")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ReciclajeController {

	 private final ReciclajeServicio reciclajeService;
	    private final MaterialRepositorio materialRepositorio;
	    private final UsuarioRepositorio usuarioRepositorio;

	    @PostMapping("/registrar")
	    public ResponseEntity<?> registrarReciclaje(
	            @RequestParam Long materialId,
	            @RequestParam(defaultValue = "1") int cantidad,
	            Authentication authentication) {
	        try {
	            String username = authentication.getName();
	            Optional<Usuario> optionalUsuario = usuarioRepositorio.findByEmail(username);
	            if (optionalUsuario.isEmpty()) {
	                return new ResponseEntity<>("Usuario no encontrado o no autorizado.", HttpStatus.UNAUTHORIZED);
	            }
	            Long usuarioId = optionalUsuario.get().getId();
	            Reciclaje reciclaje = reciclajeService.registrarReciclaje(usuarioId, materialId, cantidad);
	            return ResponseEntity.status(HttpStatus.CREATED).body(reciclaje);
	        } catch (Exception e) {
	            System.err.println("Error al registrar el reciclaje: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                        "error", "Error al registrar el reciclaje",
	                        "details", e.getMessage()
	                    ));
	        }
	    }

	    @GetMapping("/usuario/{usuarioId}")
	    public ResponseEntity<?> reciclajesPorUsuario(@PathVariable Long usuarioId) {
	        try {
	            List<Reciclaje> lista = reciclajeService.obtenerReciclajesPorUsuario(usuarioId);
	            return ResponseEntity.ok(lista);
	        } catch (Exception e) {
	            System.err.println("Error al obtener reciclajes para el usuario: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("error", "Error al obtener reciclajes para el usuario", "details", e.getMessage()));
	        }
	    }

	    @GetMapping("/mis-reciclajes")
	    public ResponseEntity<?> misReciclajes(Authentication authentication) {
	        try {
	            String username = authentication.getName();
	            Optional<Usuario> optionalUsuario = usuarioRepositorio.findByEmail(username);
	            if (optionalUsuario.isEmpty()) {
	                return new ResponseEntity<>("Usuario no encontrado o no autorizado.", HttpStatus.UNAUTHORIZED);
	            }
	            Long usuarioId = optionalUsuario.get().getId();
	            List<Reciclaje> lista = reciclajeService.obtenerReciclajesPorUsuario(usuarioId);
	            return ResponseEntity.ok(lista);
	        } catch (Exception e) {
	            System.err.println("Error al obtener tus reciclajes: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("error", "Error al obtener tus reciclajes", "details", e.getMessage()));
	        }
	    }

	    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
	    @PutMapping("/validar/{id}")
	    public ResponseEntity<?> validarReciclaje(@PathVariable Long id) {
	        try {
	            Reciclaje reciclajeValidado = reciclajeService.validarReciclaje(id);
	            return ResponseEntity.ok(reciclajeValidado);
	        } catch (Exception e) {
	            System.err.println("Error al validar el reciclaje: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                        "error", "Error al validar el reciclaje",
	                        "details", e.getMessage()
	                    ));
	        }
	    }

	    @PostMapping("/crear-desde-qr")
	    public ResponseEntity<?> crearDesdeQR(
	            @RequestBody MaterialScanResponse request,
	            Authentication authentication) {
	        try {
	            String qrData = request.getQrData();
	            int quantity = request.getQuantity();
	            if (qrData == null || qrData.isBlank()) {
	                return ResponseEntity.badRequest().body(Map.of("error", "Datos QR no válidos"));
	            }
	            if (quantity <= 0) {
	                return ResponseEntity.badRequest().body(Map.of("error", "La cantidad debe ser al menos 1"));
	            }
	            String username = authentication.getName();
	            Optional<Usuario> optionalUsuario = usuarioRepositorio.findByEmail(username);
	            if (optionalUsuario.isEmpty()) {
	                return new ResponseEntity<>("Usuario no encontrado o no autorizado.", HttpStatus.UNAUTHORIZED);
	            }
	            Long usuarioId = optionalUsuario.get().getId();
	            MaterialScanResponse response = reciclajeService.registrarReciclajeDesdeQR(usuarioId, qrData, quantity);
	            return ResponseEntity.status(HttpStatus.CREATED).body(response);
	        } catch (Exception e) {
	            System.err.println("Error al registrar desde QR: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                        "error", "Error al registrar desde QR",
	                        "details", e.getMessage()
	                    ));
	        }
	    }

	    @PostMapping("/scan-barcode")
	    public ResponseEntity<?> registrarDesdeCodigoBarras(
	            @RequestBody Map<String, String> payload,
	            Authentication authentication) {
	        try {
	            String codigo = payload.get("codigo");
	            if (codigo == null || codigo.isBlank()) {
	                return ResponseEntity.badRequest().body(Map.of("error", "Código de barras no válido"));
	            }
	            String username = authentication.getName();
	            Optional<Usuario> optionalUsuario = usuarioRepositorio.findByEmail(username);
	            if (optionalUsuario.isEmpty()) {
	                return new ResponseEntity<>("Usuario no encontrado o no autorizado.", HttpStatus.UNAUTHORIZED);
	            }
	            Long usuarioId = optionalUsuario.get().getId();
	            Reciclaje reciclaje = reciclajeService.registrarDesdeCodigoOpenFoodFacts(codigo, usuarioId);
	            return ResponseEntity.ok(Map.of(
	                "message", "Reciclaje registrado pendiente de validación",
	                "reciclaje", reciclaje
	            ));
	        } catch (Exception e) {
	            System.err.println("Error al registrar desde código de barras: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                        "error", "Error al registrar desde código de barras",
	                        "details", e.getMessage()
	                    ));
	        }
	    }

	    @PreAuthorize("hasRole('ADMIN')")
	    @GetMapping("/todos")
	    public ResponseEntity<?> obtenerTodos() {
	        try {
	            List<Reciclaje> reciclajes = reciclajeService.obtenerTodos();
	            return ResponseEntity.ok(reciclajes);
	        } catch (Exception e) {
	            System.err.println("Error al obtener todos los reciclajes: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("error", "Error al obtener todos los reciclajes", "details", e.getMessage()));
	        }
	    }

	    @PreAuthorize("hasRole('ADMIN')")
	    @GetMapping("/pendientes")
	    public ResponseEntity<?> obtenerPendientes() {
	        try {
	            List<Reciclaje> pendientes = reciclajeService.obtenerReciclajesPendientes();
	            return ResponseEntity.ok(pendientes);
	        } catch (Exception e) {
	            System.err.println("Error al obtener reciclajes pendientes: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("error", "Error al obtener reciclajes pendientes", "details", e.getMessage()));
	        }
	    }
}