package com.example.reciclaje.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID; // Importar UUID para generar semillas únicas si es necesario

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
    
    // Estilo de DiceBear a utilizar. Puedes cambiarlo a "lorelei", "avataaars", "fun-emoji", etc.
    private static final String DICEBEAR_STYLE = "bottts";

    // Método para generar la URL del avatar de DiceBear
    private String generarDiceBearAvatarUrl(String seed) {
        // La semilla debe ser una cadena de texto única para cada usuario.
        // Se recomienda usar el ID del usuario o su email.
        // Importante: Escapar el '%' en la URL de DiceBear con '%%' si no es un especificador de formato.
        // La URL de DiceBear es: https://api.dicebear.com/7.x/<estilo>/svg?seed=<semilla>
        // Si el estilo o la semilla contuvieran un '%', String.format() lo interpretaría mal.
        // Sin embargo, en la URL base de DiceBear, el '7.x' no tiene '%'.
        // El problema puede venir si la 'seed' o el 'DICEBEAR_STYLE' contienen un '%'.
        // Para evitar problemas, podemos usar String.replace() o concatenación simple.
        // Pero si insistes en String.format, la forma correcta es:
        // return String.format("https://api.dicebear.com/7.x/%s/svg?seed=%s", DICEBEAR_STYLE, seed);
        // El error "Format specifier '%s'" sugiere que el problema está en la cadena de formato en sí.
        // Una forma más segura es la concatenación simple si no hay otros %s necesarios:
        return "https://api.dicebear.com/7.x/" + DICEBEAR_STYLE + "/svg?seed=" + seed;
        // Si por alguna razón necesitas String.format y un % aparece en DICEBEAR_STYLE o seed,
        // tendrías que escapar esos valores antes de pasarlos a String.format.
        // Por ejemplo: String escapedSeed = seed.replace("%", "%%");
        // Pero la concatenación es más simple y segura para este caso.
    }

    @Transactional
    public UsuarioDTO registrarUsuarioConRol(Usuario usuario) {
    	
    	  log.info("Intentando registrar usuario con datos:");
    	    log.info("Nombre: {}", usuario.getNombre());
    	    log.info("Email: {}", usuario.getEmail());
    	    log.info("Dirección: {}", usuario.getDireccion());
    	    log.info("Teléfono: {}", usuario.getTelefono());

    	    if (usuario.getDireccion() == null) {
    	        log.warn("La dirección es null, asignando valor por defecto");
    	        usuario.setDireccion("Sin dirección especificada");
    	    }
    	    
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setPuntos(0);
        usuario.setDireccion(usuario.getDireccion() != null ? usuario.getDireccion() : ""); // Valor por defecto si es null

        if (usuario.getNivel() == null) {
            Nivel nivelInicial = nivelRepository.findByPuntosMinimos(0)
                .orElseThrow(() -> new IllegalStateException("Nivel inicial no encontrado"));
            usuario.setNivel(nivelInicial);
        }
        
        Rol rolUser = rolRepositorio.findByNombre("USER")
            .orElseThrow(() -> new IllegalStateException("Rol USER no encontrado"));
        
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rolUser);
        usuarioRol.setActivo(true);
        usuario.getUsuarioRoles().add(usuarioRol);

        // Asignar avatar de DiceBear al registrar el usuario
        // Usamos el email como semilla porque el ID del usuario podría no estar disponible aún
        // si el usuario no ha sido guardado en la DB por primera vez.
        usuario.setAvatarId(generarDiceBearAvatarUrl(usuario.getEmail()));
        
        

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        log.info("Usuario guardado con ID: {}, Dirección: {}", 
                usuarioGuardado.getId(), usuarioGuardado.getDireccion());
        
        
        return convertirAUsuarioDTO(usuarioGuardado);
    }
    
    // Método para actualizar el perfil del usuario, incluyendo el avatar
    @Transactional
    public Usuario actualizarPerfil(Usuario usuarioActualizado) {
    	
    	
        Usuario usuarioExistente = usuarioRepository.findById(usuarioActualizado.getId())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Actualizar campos básicos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());

        // Manejo del avatar:
        // Si el avatarId en usuarioActualizado es nulo o vacío, significa que no se subió una nueva foto
        // o se pidió "eliminar" la foto actual. En ese caso, reasignamos un avatar de DiceBear.
        // Si tiene un valor, asumimos que es una URL de una foto subida o el DiceBear existente.
        if (usuarioActualizado.getAvatarId() == null || usuarioActualizado.getAvatarId().isEmpty()) {
            // Si el usuario ya tenía un avatar de DiceBear, se mantendrá el mismo (misma semilla)
            // Si tenía una foto subida y ahora la eliminó, se le asigna un DiceBear.
            usuarioExistente.setAvatarId(generarDiceBearAvatarUrl(usuarioExistente.getEmail()));
        } else {
            // Si se proporcionó una URL de avatar (ej. una foto subida), la usamos.
            usuarioExistente.setAvatarId(usuarioActualizado.getAvatarId());
        }

        // Si la contraseña se actualiza
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }
       
       
      

        return usuarioRepository.save(usuarioExistente);
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO(); // Crear una nueva instancia
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
       dto.setDireccion(usuario.getDireccion());
        dto.setPuntos(usuario.getPuntos());
        dto.setAvatarId(usuario.getAvatarId()); // Asegurar que el avatarId se mapea
        if (usuario.getNivel() != null) {
            dto.setNombre(usuario.getNivel().getNombre());
        }
        // ... otros mapeos de campos si los hay en tu DTO
        return dto;
    }
    
    private Rol obtenerOCrearRol(String nombre, String descripcion) {
        return rolRepositorio.findByNombre(nombre)
            .orElseGet(() -> rolRepositorio.save(new Rol ( nombre, descripcion))
            );
    }

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

    public Usuario crearUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }
}
