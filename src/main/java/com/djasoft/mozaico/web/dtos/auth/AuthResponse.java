package com.djasoft.mozaico.web.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación")
public class AuthResponse {

    @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Tiempo de expiración en segundos", example = "900")
    private Integer expiresIn;

    @Schema(description = "Información del usuario autenticado")
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del usuario")
    public static class UserInfo {
        @Schema(description = "ID del usuario", example = "1")
        private Long id;

        @Schema(description = "Nombre de usuario", example = "admin")
        private String username;

        @Schema(description = "Nombre completo", example = "Administrador Principal")
        private String nombre;

        @Schema(description = "Email del usuario", example = "admin@empresa.com")
        private String email;

        @Schema(description = "Tipo de usuario", example = "ADMIN")
        private String tipoUsuario;

        @Schema(description = "Nombre display del tipo de usuario", example = "Administrador")
        private String tipoUsuarioDisplayName;

        @Schema(description = "ID de la empresa", example = "1")
        private Long empresaId;

        @Schema(description = "Nombre de la empresa", example = "Mi Restaurante")
        private String empresaNombre;

        @Schema(description = "Permisos del usuario")
        private Set<String> permissions;
    }
}