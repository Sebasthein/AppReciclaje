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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class MaterialServicio {

	private final MaterialRepositorio materialRepository;
	private final ObjectMapper objectMapper; // Añadir para inyectar ObjectMapper

	// Modificar constructor para inyectar ObjectMapper
	public MaterialServicio(MaterialRepositorio materialRepository, ObjectMapper objectMapper) {
		this.materialRepository = materialRepository;
		this.objectMapper = objectMapper;
	}

	@PostConstruct
	public void inicializarMateriales() {
		if (materialRepository.count() == 0) {
			List<Material> materiales = Arrays.asList(
					crearMaterialPorDefecto("Botella PET", "Plástico", 10, "00001", "botella-pet.jpg"),
					crearMaterialPorDefecto("Latas Aluminio", "Metal", 15, "00002", "latas.jpg"),
					crearMaterialPorDefecto("Cartón Corrugado", "Papel", 8, "00003", "carton.jpg"),
					crearMaterialPorDefecto("Vidrio Transparente", "Vidrio", 12, "00004", "vidrio.jpg"));
			materialRepository.saveAll(materiales);
		}
	}

	// Renombrado para mayor claridad, ya que es para materiales por defecto
	private Material crearMaterialPorDefecto(String nombre, String categoria, int puntos, String codigo, String imagenUrl) {
		return Material.builder()
				.nombre(nombre)
				.categoria(categoria)
				.puntosPorUnidad(puntos)
				.codigoBarra(codigo)
				.reciclable(true)
				.descripcion("Material para reciclaje de " + categoria) // Considera si 'description' o 'descripcion'
				// .imagenUrl(imagenUrl) // Descomenta si usas imagenUrl
				.build();
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

		// Buscar por codigoBarra antes de guardar para evitar duplicados
		materialRepository.findByCodigoBarra(material.getCodigoBarra()).ifPresent(m -> {
			throw new DataIntegrityViolationException("Código de barras ya registrado: " + material.getCodigoBarra());
		});

		// Asegurar valores por defecto si no vienen en el request
		if (material.getPuntosPorUnidad() == null) {
			material.setPuntosPorUnidad(10);
		}
		if (material.getReciclable() == null) {
			material.setReciclable(true);
		}
		// Considera si 'description' también debería tener un valor por defecto si no viene
		if (material.getDescripcion() == null || material.getDescripcion().trim().isEmpty()) {
		    material.setDescripcion("Material para reciclaje de " + material.getCategoria());
		}


		return materialRepository.save(material);
	}

	@Transactional
	public Material actualizarMaterial(Long id, Material material) {
		Material existente = materialRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Material no encontrado con ID: " + id)); // Mensaje más descriptivo

		// Actualizar solo los campos que se permiten actualizar
		existente.setNombre(material.getNombre());
		existente.setDescripcion(material.getDescripcion());
		existente.setCategoria(material.getCategoria());
		existente.setPuntosPorUnidad(material.getPuntosPorUnidad());
		existente.setReciclable(material.getReciclable());
		// No permitir actualizar codigoBarra aquí si se supone que es inmutable una vez creado
		// o añadir lógica específica para ello si es un caso de uso.

		return materialRepository.save(existente);
	}

	@Transactional(readOnly = true)
	public List<Material> obtenerTodos() {
		return materialRepository.findAll();
	}
	
	@Transactional
	public Material crearMaterialDesdeQR(String qrData) {
		try {
			// Parsear el JSON del QR usando el ObjectMapper inyectado
			JsonNode jsonNode = objectMapper.readTree(qrData);
			
			// Validar campos obligatorios
			if (!jsonNode.hasNonNull("nombre") || !jsonNode.hasNonNull("categoria") || !jsonNode.hasNonNull("puntosPorUnidad")) {
				throw new IllegalArgumentException("El QR no contiene todos los campos obligatorios: nombre, categoria, puntosPorUnidad.");
			}
			
			// Crear el material
			Material material = new Material();
			material.setNombre(jsonNode.get("nombre").asText());
			material.setCategoria(jsonNode.get("categoria").asText());
			material.setPuntosPorUnidad(jsonNode.get("puntosPorUnidad").asInt());
			
			// Campos opcionales
			if (jsonNode.hasNonNull("descripcion")) { // Usar hasNonNull para asegurar que el valor no es null
				material.setDescripcion(jsonNode.get("descripcion").asText());
			} else {
				material.setDescripcion("Material para reciclaje de " + material.getCategoria());
			}
			
			if (jsonNode.hasNonNull("codigoBarra")) {
				material.setCodigoBarra(jsonNode.get("codigoBarra").asText());
			}
			
			if (jsonNode.hasNonNull("reciclable")) {
				material.setReciclable(jsonNode.get("reciclable").asBoolean());
			} else {
				material.setReciclable(true); // Valor por defecto
			}
			
			// Validar y guardar usando el método existente para evitar duplicación de lógica
			return crearMaterial(material);
			
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Formato de QR inválido o malformado: " + e.getMessage(), e);
		}
	}
}
