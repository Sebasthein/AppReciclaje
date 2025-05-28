package com.example.reciclaje.servicioDTO;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UsuarioLogroId implements Serializable {
	  @Column(name = "usuario_id")
	    private Long usuarioId;

	    @Column(name = "logro_id")
	    private Long logroId;

    public UsuarioLogroId() {}

    public UsuarioLogroId(Long usuarioId, Long logroId) {
        this.usuarioId = usuarioId;
        this.logroId = logroId;
    }

    // Getters y setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLogroId() { return logroId; }
    public void setLogroId(Long logroId) { this.logroId = logroId; }

    // equals y hashCode (importante para clave compuesta)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioLogroId)) return false;
        UsuarioLogroId that = (UsuarioLogroId) o;
        return usuarioId.equals(that.usuarioId) && logroId.equals(that.logroId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(usuarioId, logroId);
    }
}
