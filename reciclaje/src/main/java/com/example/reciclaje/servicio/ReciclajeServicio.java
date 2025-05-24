package com.example.reciclaje.servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.*;
import com.example.reciclaje.repositorio.*;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReciclajeServicio {

    private final OpenFoodFactsService openFoodFactsService_1;
    private final ReciclajeRepositorio reciclajeRepository;
    private final UsuarioServicio usuarioService;
    private final MaterialRepositorio materialRepository;
    private final NivelRepositorio nivelRepository;
    private final UsuarioRepositorio usuarioRepository;
    private final LogService logService;
    private final LogroServicio logroServicio;
    private final NivelServicio nivelService;
    private final OpenFoodFactsService openFoodFactsService;
    private final MaterialServicio materialService;

    @Transactional
    public Reciclaje registrarReciclaje(Long usuarioId, Long materialId, int cantidad) {
        Usuario usuario = usuarioService.obtenerUsuario(usuarioId);
        Material material = materialRepository.findById(materialId)
            .orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));

        Reciclaje reciclaje = new Reciclaje();
        reciclaje.setUsuario(usuario);
        reciclaje.setMaterial(material);
        reciclaje.setCantidad(cantidad);
        reciclaje.setFecha(LocalDateTime.now());
        reciclaje.setPuntosObtenidos(material.getPuntosPorUnidad() * cantidad);
        reciclaje.setValidado(false);

        reciclaje = reciclajeRepository.save(reciclaje);

        // Solo actualizar puntos si está validado inmediatamente
        if (reciclaje.getValidado()) {
            usuario.setPuntos(usuario.getPuntos() + reciclaje.getPuntosObtenidos());
            actualizarNivelYLogros(usuario);
            usuarioRepository.save(usuario);
        }

        return reciclaje;
    }

   
    public Reciclaje registrarDesdeCodigo(String codigoBarras, Long usuarioId, String packaging) {
        // Paso 1: Buscar el producto en Open Food Facts
        JsonNode productData = openFoodFactsService.getProductByBarcode(codigoBarras);
        
        if (productData.get("status").asInt() != 1) {
            throw new RuntimeException("Producto no encontrado en Open Food Facts");
        }

        JsonNode product = productData.get("product");
        
        // Paso 2: Extraer datos relevantes
        String nombre = product.get("product_name").asText();
        String categoria = product.get("categories").asText().split(",")[0];
        String imagenUrl = product.get("image_url").asText();
        
        // Paso 3: Determinar el material (ej: PET, vidrio, etc.)
        String materialTipo = determinarMaterialDesdePackaging(product.get("packaging").asText());
        
        // Paso 4: Buscar o crear el material en tu BD
        List<Material> materialesExistentes = materialService.buscarPorTipo(materialTipo);
        Material material;

        if (materialesExistentes.isEmpty()) {
            // Crear nuevo material si no existe
            material = materialService.crearMaterial(
                Material.builder()
                    .nombre(nombre)
                    .description("Envase de " + materialTipo + ". " + product.get("brands").asText())
                    .categoria(categoria)
                   // .imagenUrl(imagenUrl)
                    .puntosPorUnidad(10)
                    .reciclable(true)
                    .codigoBarra(codigoBarras)
                    .build()
            );
        } else {
            // Usar el primer material encontrado
            material = materialesExistentes.get(0);
        }
        
        // Paso 5: Registrar el reciclaje
        return registrarReciclaje(usuarioId, material.getId(), 1);
    }

    private String determinarMaterialDesdePackaging(String packaging) {
        packaging = packaging.toLowerCase();
        if (packaging.contains("pet") || packaging.contains("plástico")) {
            return "PET";
        } else if (packaging.contains("vidrio") || packaging.contains("glass")) {
            return "Vidrio";
        } else if (packaging.contains("aluminio") || packaging.contains("aluminum")) {
            return "Aluminio";
        }
        return "Otros";
    }

    @Transactional
    public Reciclaje validarReciclaje(Long id) {
        Reciclaje reciclaje = reciclajeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reciclaje no encontrado"));

        if (reciclaje.getValidado()) {
            throw new IllegalStateException("El reciclaje ya fue validado");
        }

        Usuario usuario = reciclaje.getUsuario();
        usuario.setPuntos(usuario.getPuntos() + reciclaje.getPuntosObtenidos());
        
        reciclaje.setValidado(true);
        reciclaje.setFechaValidacion(LocalDateTime.now());
        
        actualizarNivelYLogros(usuario);
        
        usuarioRepository.save(usuario);
        return reciclajeRepository.save(reciclaje);
    }

    private void actualizarNivelYLogros(Usuario usuario) {
        Nivel nuevoNivel = nivelService.obtenerNivelPorPuntos(usuario.getPuntos());
        
        if (!nuevoNivel.equals(usuario.getNivel())) {
            usuario.setNivel(nuevoNivel);
            usuarioRepository.save(usuario);
            verificarLogrosNuevoNivel(usuario, nuevoNivel);
        }
    }

    private void verificarLogrosNuevoNivel(Usuario usuario, Nivel nivel) {
        if (nivel.getLogro() != null && !usuario.getLogros().contains(nivel.getLogro())) {
            usuario.getLogros().add(nivel.getLogro());
            usuarioRepository.save(usuario);
            // Aquí podrías agregar notificación al usuario
        }
    }

    public List<Reciclaje> obtenerReciclajesPorUsuario(Long usuarioId) {
        return reciclajeRepository.findByUsuarioId(usuarioId);
    }

    public List<Reciclaje> obtenerTodos() {
        return reciclajeRepository.findAll();
    }

	public List<Reciclaje> obtenerReciclajesPendientes() {
    return reciclajeRepository.findByValidadoFalse();
}
}