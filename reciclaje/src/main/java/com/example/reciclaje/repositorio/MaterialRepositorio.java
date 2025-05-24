package com.example.reciclaje.repositorio;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reciclaje.entidades.Material;

public interface MaterialRepositorio extends JpaRepository<Material, Long> {
	// boolean existsByNombre(String nombre); // Eliminar si no se usa
    // Optional<Material> findByNombre(String nombre); // Eliminar si no se usa
    // Optional<Material> findByNombreContainingIgnoreCase(String nombre); // Eliminar si no se usa
    Optional<Material> findByCodigoBarra(String codigoBarra); // Usado en MaterialServicio
    // Optional<Material> findFirstByCategoria(String categoria); // Eliminar si no se usa
    // List<Material> findByNombreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nombre, String description); // Eliminar si no se usa
    List<Material> findByCategoriaContainingIgnoreCase(String tipo); // Usado en MaterialServicio
}
