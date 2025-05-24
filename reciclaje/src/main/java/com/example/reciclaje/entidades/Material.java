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
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "materiales")
@Data
@Getter
@Setter
@Builder

@AllArgsConstructor
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String description;
    private String categoria;
    private Integer puntosPorUnidad;
   // private String imagenUrl; // Asegúrate de tener este campo
    private Boolean reciclable;
    
    @Column(name = "codigo_barra", unique = true)
    private String codigoBarra;

    // Constructor explícito con todos los campos (incluyendo id)



    @OneToMany(mappedBy = "material")
    private List<Reciclaje> reciclajes;

 // Constructor completo
   public Material(String nombre, String description, String categoria, 
                   String imagenUrl, int puntosPorUnidad, boolean reciclable, 
                   String codigoBarra) {
        this.nombre = nombre;
        this.description = description;
        this.categoria = categoria;
      //  this.imagenUrl = imagenUrl;
        this.puntosPorUnidad = puntosPorUnidad;
        this.reciclable = reciclable;
        this.codigoBarra = codigoBarra;
    }

    public Material(Long id, String nombre, String categoria, int puntosPorUnidad, String imagenUrl, String codigoBarra) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.puntosPorUnidad = puntosPorUnidad;
      //  this.imagenUrl = imagenUrl;
        this.codigoBarra = codigoBarra;
        this.reciclable = true; // Valor por defecto
        this.description = "";  // Valor por defecto
    }
    // Constructor vacío (necesario para JPA)
    public Material() {
    }

	
}