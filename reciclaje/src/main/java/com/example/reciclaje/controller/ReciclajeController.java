package com.example.reciclaje.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.reciclaje.entidades.Reciclaje;
import com.example.reciclaje.seguridad.CustomUserDetails;
import com.example.reciclaje.servicio.ReciclajeServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reciclajes")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ReciclajeController {

	private final ReciclajeServicio reciclajeService;

    // Constructor explícito eliminado gracias a @RequiredArgsConstructor

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarReciclaje(
            @RequestParam Long materialId,
            @RequestParam(defaultValue = "1") int cantidad,
            Authentication authentication) {
        
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long usuarioId = userDetails.getId();
            
            Reciclaje reciclaje = reciclajeService.registrarReciclaje(usuarioId, materialId, cantidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(reciclaje);
        } catch (Exception e) {
            // Podrías crear excepciones personalizadas en el servicio
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
            // Considera devolver 404 solo si no se encuentra el usuario/data específica
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // Cambiado a INTERNAL_SERVER_ERROR
                    .body(Map.of("error", "Error al obtener reciclajes para el usuario", "details", e.getMessage()));
        }
    }

    @GetMapping("/mis-reciclajes")
    public ResponseEntity<?> misReciclajes(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<Reciclaje> lista = reciclajeService.obtenerReciclajesPorUsuario(userDetails.getId());
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // Cambiado a INTERNAL_SERVER_ERROR
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Error al validar el reciclaje",
                        "details", e.getMessage()
                    ));
        }
    }
    
    @PostMapping("/scan-qr")
    public ResponseEntity<?> registrarDesdeQR(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        try {
            String codigo = payload.get("codigo");
            int cantidad = Integer.parseInt(payload.getOrDefault("cantidad", "1"));

            if (codigo == null || codigo.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Código QR no válido"));
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Opción 1: Si usas códigos MATERIAL_XX
            if (codigo.startsWith("MATERIAL_")) {
                Long materialId = Long.parseLong(codigo.replace("MATERIAL_", ""));
                Reciclaje reciclaje = reciclajeService.registrarReciclaje(userDetails.getId(), materialId, cantidad);
                return ResponseEntity.ok(reciclaje);
            }
            
            // Opción 2: Si usas códigos directos
            // ¿Estás seguro de que quieres usar este método si el código no empieza con "MATERIAL_"?
            // Podrías considerar una lógica más robusta aquí.
            Reciclaje reciclaje = reciclajeService.registrarDesdeCodigo(codigo, userDetails.getId(), null);
            return ResponseEntity.ok(reciclaje);

        } catch (NumberFormatException e) { // Específico para el parseo de cantidad
            return ResponseEntity.badRequest().body(Map.of("error", "Cantidad no válida", "details", e.getMessage()));
        } catch (Exception e) {
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
            String packaging = payload.get("packaging");

            if (codigo == null || codigo.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Código de barras no válido"));
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Reciclaje reciclaje = reciclajeService.registrarDesdeCodigo(codigo, userDetails.getId(), packaging);
            
            return ResponseEntity.ok(Map.of(
                "message", "Reciclaje registrado pendiente de validación",
                "reciclaje", reciclaje
            ));
            
        } catch (Exception e) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener reciclajes pendientes", "details", e.getMessage()));
        }
    }
}