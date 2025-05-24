package com.example.reciclaje.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

public class MaterialController {

	private final MaterialServicio materialService;

    public MaterialController(MaterialServicio materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ResponseEntity<List<Material>> listarMateriales() {
        return ResponseEntity.ok(materialService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> obtenerMaterial(@PathVariable Long id) {
        return materialService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Material>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(materialService.buscarPorTipo(tipo));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Material> crearMaterial(@RequestBody Material material) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(materialService.crearMaterial(material));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Material> actualizarMaterial(
            @PathVariable Long id,
            @RequestBody Material material) {
        return ResponseEntity.ok(materialService.actualizarMaterial(id, material));
    }
}
