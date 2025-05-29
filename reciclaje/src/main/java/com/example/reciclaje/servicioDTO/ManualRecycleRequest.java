package com.example.reciclaje.servicioDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Lombok para getters/setters
public class ManualRecycleRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @Min(value = 1, message = "Los puntos por unidad deben ser al menos 1")
    private int puntosPorUnidad;
    
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;
}