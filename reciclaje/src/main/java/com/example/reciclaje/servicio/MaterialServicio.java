package com.example.reciclaje.servicio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reciclaje.entidades.Material;
import com.example.reciclaje.repositorio.MaterialRepositorio;

import jakarta.annotation.PostConstruct;

@Service
public class MaterialServicio {

	private final MaterialRepositorio materialRepository;

	public MaterialServicio(MaterialRepositorio materialRepository) {
		this.materialRepository = materialRepository;
	}

	@PostConstruct
	public void inicializarMateriales() {
		if (materialRepository.count() == 0) {
			List<Material> materiales = Arrays.asList(
					crearMaterial("Botella PET", "Plástico", 10, "00001", "botella-pet.jpg"),
					crearMaterial("Latas Aluminio", "Metal", 15, "00002", "latas.jpg"),
					crearMaterial("Cartón Corrugado", "Papel", 8, "00003", "carton.jpg"),
					crearMaterial("Vidrio Transparente", "Vidrio", 12, "00004", "vidrio.jpg"));
			materialRepository.saveAll(materiales);
		}
	}

	private Material crearMaterial(String nombre, String categoria, int puntos, String codigo, String imagenUrl) {
		return Material.builder().nombre(nombre).categoria(categoria).puntosPorUnidad(puntos).codigoBarra(codigo)
				.reciclable(true).description("Material para reciclaje de " + categoria).build();
	}

	@Transactional(readOnly = true)
	public Optional<Material> buscarPorId(Long id) {
		return materialRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Material> buscarPorTipo(String tipo) {
		return materialRepository.findByCategoriaContainingIgnoreCase(tipo);
	}

	@Transactional
	public Material crearMaterial(Material material) {
		if (material.getNombre() == null || material.getNombre().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre del material no puede estar vacío");
		}

		materialRepository.findByCodigoBarra(material.getCodigoBarra()).ifPresent(m -> {
			throw new DataIntegrityViolationException("Código de barras ya registrado: " + material.getCodigoBarra());
		});

		if (material.getPuntosPorUnidad() == null)
			material.setPuntosPorUnidad(10);
		if (material.getReciclable() == null)
			material.setReciclable(true);

		return materialRepository.save(material);
	}

	@Transactional
	public Material actualizarMaterial(Long id, Material material) {
		Material existente = materialRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));

		existente.setNombre(material.getNombre());
		existente.setDescription(material.getDescription());
		existente.setCategoria(material.getCategoria());
		existente.setPuntosPorUnidad(material.getPuntosPorUnidad());
		existente.setReciclable(material.getReciclable());

		return materialRepository.save(existente);
	}

	@Transactional(readOnly = true)
	public List<Material> obtenerTodos() {
		return materialRepository.findAll();
	}
}
