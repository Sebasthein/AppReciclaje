package com.example.reciclaje.entidades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	private String nombre;
	@EqualsAndHashCode.Include
	private String email;
	private String password;
	private int puntos;

	 @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "nivel_id")
	    private Nivel nivel;

	    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	    private List<Reciclaje> reciclajes = new ArrayList<>();

	    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

	    @ManyToMany(mappedBy = "usuarios")
	    private Set<Logro> logros = new HashSet<>();

	    @ManyToMany(fetch = FetchType.EAGER)
	    @JoinTable(
	        name = "usuario_roles",
	        joinColumns = @JoinColumn(name = "usuario_id"),
	        inverseJoinColumns = @JoinColumn(name = "rol_id")
	    )

	    private Set<Rol> roles = new HashSet<>();
	    
	    // Método getRoles corregido
	    public Set<Rol> getRoles() {
	        return roles;
	    }
	   
	    // UserDetails methods
	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        Set<GrantedAuthority> authorities = new HashSet<>();
	        for (UsuarioRol usuarioRol : usuarioRoles) {
	            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuarioRol.getRol().getNombre()));
	        }
	        return authorities;
	    }

	    @Override
	    public String getUsername() {
	        return email;
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }
}