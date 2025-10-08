package com.djasoft.mozaico.security;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Servicio de seguridad para validaciones de roles y permisos
 */
@Service
public class SecurityService {

    /**
     * Verifica si el usuario actual tiene uno de los tipos de usuario especificados
     *
     * @param tiposPermitidos Tipos de usuario permitidos (ADMIN, SUPER_ADMIN, etc.)
     * @return true si el usuario tiene uno de los tipos, false si no
     */
    public boolean isTipoUsuario(String... tiposPermitidos) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();

        if (currentUser == null) {
            return false;
        }

        TipoUsuario tipoActual = currentUser.getTipoUsuario();

        return Arrays.stream(tiposPermitidos)
                .anyMatch(tipo -> tipoActual == TipoUsuario.valueOf(tipo));
    }

    /**
     * Verifica si el usuario actual es SUPER_ADMIN
     */
    public boolean isSuperAdmin() {
        return isTipoUsuario("SUPER_ADMIN");
    }

    /**
     * Verifica si el usuario actual es ADMIN o SUPER_ADMIN
     */
    public boolean isAdmin() {
        return isTipoUsuario("ADMIN", "SUPER_ADMIN");
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    public Usuario getCurrentUser() {
        return JwtAuthenticationFilter.getCurrentUser();
    }
}
