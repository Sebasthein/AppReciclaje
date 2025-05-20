package com.example.reciclaje.servicio;

import java.time.LocalDate;
import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import com.example.reciclaje.entidades.Nivel;
import com.example.reciclaje.entidades.Rol;
import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.entidades.UsuarioRol;
import com.example.reciclaje.repositorio.NivelRepositorio;
import com.example.reciclaje.repositorio.RolRepositorio;
import com.example.reciclaje.repositorio.UsuarioRepositorio;
import com.example.reciclaje.servicioDTO.UsuarioDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepository;
    private final NivelRepositorio nivelRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepositorio rolRepositorio;

    // Lista de emails que tendrán rol ADMIN (podría moverse a application.properties)
    private static final Set<String> ADMIN_EMAILS = Set.of(
        "jahir@gmail.com", 
        "sebas@gmail.com"
    );

    @Transactional
    public UsuarioDTO registrarUsuarioConRol(Usuario usuario) {
        // Validación de email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Configuración inicial
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setPuntos(0);

        // Asignar nivel inicial
        var nivelInicial = nivelRepository.findByPuntosMinimos(0)
                .orElseThrow(() -> new IllegalStateException("Nivel inicial no configurado"));
        usuario.setNivel(nivelInicial);

        // Configuración de roles
        var rolUser = obtenerOCrearRol("USER", "Rol de usuario normal");
        var rolAdmin = obtenerOCrearRol("ADMIN", "Rol de administrador");

        // Crear relación UsuarioRol
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setActivo(true);
        usuarioRol.setFechaAsignacion(LocalDate.now());
        usuarioRol.setRol(ADMIN_EMAILS.contains(usuario.getEmail().toLowerCase()) ? rolAdmin : rolUser);

        usuario.setUsuarioRoles(Set.of(usuarioRol));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Convertir a DTO
        return convertirAUsuarioDTO(usuarioGuardado);
    }
    
    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO(usuario);
        // Map fields from Usuario to UsuarioDTO
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        // ... other field mappings
        return dto;
    }
    
    private Rol obtenerOCrearRol(String nombre, String descripcion) {
        return rolRepositorio.findByNombre(nombre)
            .orElseGet(() -> rolRepositorio.save(new Rol ( nombre, descripcion))
            );
    }

    // Mantén los demás métodos existentes...
    public Usuario obtenerPerfil(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario obtenerPerfilPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Nivel obtenerNivelPorPuntos(int puntos) {
        List<Nivel> niveles = nivelRepository.findAll()
                .stream()
                .sorted((n1, n2) -> Integer.compare(n2.getPuntosMinimos(), n1.getPuntosMinimos()))
                .toList();

        for (Nivel nivel : niveles) {
            if (puntos >= nivel.getPuntosMinimos()) {
                return nivel;
            }
        }

        return niveles.isEmpty() ? null : niveles.get(niveles.size() - 1);
    }

    public Usuario agregarPuntos(Long usuarioId, int puntosGanados) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPuntos(usuario.getPuntos() + puntosGanados);
        actualizarNivel(usuario);

        return usuarioRepository.save(usuario);
    }

    public void actualizarNivel(Usuario usuario) {
        usuario.setNivel(obtenerNivelPorPuntos(usuario.getPuntos()));
        usuarioRepository.save(usuario);
    }

    public int obtenerPuntosPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return usuario.getPuntos();
    }

    // Añade este método
    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existeNombre(String nombre) {
        return usuarioRepository.existsByNombre(nombre);
    }
 /*   @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    // Al crear usuario
    public Usuario crearUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}

