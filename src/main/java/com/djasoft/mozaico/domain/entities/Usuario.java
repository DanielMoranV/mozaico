package com.djasoft.mozaico.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.djasoft.mozaico.domain.enums.usuario.EstadoUsuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "id_empresa"}),
        @UniqueConstraint(columnNames = {"email", "id_empresa"}),
        @UniqueConstraint(columnNames = {"numero_documento_identidad", "id_empresa"})
    })
@EqualsAndHashCode(exclude = {"empresa"})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "numero_documento_identidad", nullable = false, length = 20)
    private String numeroDocumentoIdentidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento_identidad", nullable = false, length = 20)
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    @Builder.Default
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "ip_ultimo_acceso", length = 45)
    private String ipUltimoAcceso;

    // === RELACIÓN CON EMPRESA ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    // === CAMPOS ADICIONALES PARA JWT ===
    @Column(name = "token_version")
    @Builder.Default
    private Long tokenVersion = 0L;

    @Column(name = "ultimo_token_jwt", columnDefinition = "TEXT")
    private String ultimoTokenJwt;

    // Using @CreationTimestamp for created_at as well
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // @UpdateTimestamp handles the "ON UPDATE" behavior
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === IMPLEMENTACIÓN DE UserDetails ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return tipoUsuario.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority("ROLE_" + permission))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return estado == EstadoUsuario.ACTIVO;
    }

    @Override
    public boolean isAccountNonLocked() {
        return intentosFallidos < 5; // Bloquear después de 5 intentos fallidos
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado == EstadoUsuario.ACTIVO;
    }

    // === MÉTODOS DE UTILIDAD ===
    public boolean hasPermission(String permission) {
        return tipoUsuario.hasPermission(permission);
    }

    public boolean belongsToCompany(Long empresaId) {
        return empresa != null && empresa.getIdEmpresa().equals(empresaId);
    }

    public void incrementFailedAttempts() {
        this.intentosFallidos = (this.intentosFallidos == null) ? 1 : this.intentosFallidos + 1;
    }

    public void resetFailedAttempts() {
        this.intentosFallidos = 0;
    }

    public void updateLastAccess(String ipAddress) {
        this.fechaUltimoAcceso = LocalDateTime.now();
        this.ipUltimoAcceso = ipAddress;
    }

    public void invalidateTokens() {
        this.tokenVersion = (this.tokenVersion == null) ? 1L : this.tokenVersion + 1;
        this.ultimoTokenJwt = null;
    }
}
