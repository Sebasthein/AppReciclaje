package com.example.reciclaje.repositorio;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reciclaje.entidades.Material;

public interface MaterialRepositorio extends JpaRepository<Material, Long> {
    boolean existsByNombre(String nombre);
    Optional<Material> findByNombre(String nombre);
    Optional<Material> findByNombreContainingIgnoreCase(String nombre);
    Optional<Material> findByCodigoBarra(String codigoBarra);
    Optional<Material> findFirstByCategoria(String categoria);
	 // Busca por nombre O descripción (ignorando mayúsculas/minúsculas)
    List<Material> findByNombreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nombre, String description
    );
	List<Material> findByCategoriaContainingIgnoreCase(String tipo);
	
}
