package com.example.reciclaje.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reciclaje.entidades.Rol;

public interface RolRepositorio extends JpaRepository<Rol, Long> {

	  Optional<Rol> findByNombre(String nombre);
}
