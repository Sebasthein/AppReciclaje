package com.example.reciclaje.servicio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.Material;
import com.example.reciclaje.repositorio.MaterialRepositorio;

@Service
public class MaterialServicio {

	 private final MaterialRepositorio materialRepository;

	    public MaterialServicio(MaterialRepositorio materialRepository) {
	        this.materialRepository = materialRepository;
	        this.inicializarMateriales();
	    }

	    private void inicializarMateriales() {
	        if (materialRepository.count() == 0) {
	            List<Material> materiales = new ArrayList<>();
	            materiales.add(new Material(null, "Botella PET", "Plástico", 10, "botella-pet.jpg"));
	            materiales.add(new Material(null, "Latas Aluminio", "Metal", 15, "latas.jpg"));
	            materiales.add(new Material(null, "Cartón Corrugado", "Papel", 8, "carton.jpg"));
	            materiales.add(new Material(null, "Vidrio Transparente", "Vidrio", 12, "vidrio.jpg"));
	            materialRepository.saveAll(materiales);
	        }
	    }
}
