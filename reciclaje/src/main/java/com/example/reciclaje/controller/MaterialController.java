package com.example.reciclaje.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.reciclaje.entidades.Material;
import com.example.reciclaje.repositorio.MaterialRepositorio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
public class MaterialController {

	private final MaterialRepositorio materialRepository;

    @GetMapping
    public List<Material> listarMateriales() {
        return materialRepository.findAll();
    }
}
