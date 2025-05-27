package com.example.reciclaje.entidades;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "materiales")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String categoria;
    private int puntosPorUnidad;
    private String imagenUrl; // Asegúrate de tener este campo

    // Constructor completo (debe coincidir con lo que usas)
    public Material(Long id, String nombre, String categoria, int puntosPorUnidad, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.puntosPorUnidad = puntosPorUnidad;
        this.imagenUrl = imagenUrl;
    }


    @OneToMany(mappedBy = "material")
    private List<Reciclaje> reciclajes;
}