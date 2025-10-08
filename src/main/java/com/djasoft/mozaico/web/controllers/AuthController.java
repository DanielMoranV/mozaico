package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.services.AuthService;
import com.djasoft.mozaico.web.dtos.auth.AuthRequest;
import com.djasoft.mozaico.web.dtos.auth.AuthResponse;
import com.djasoft.mozaico.web.dtos.auth.LogoutRequest;
import com.djasoft.mozaico.web.dtos.auth.RefreshTokenRequest;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "423", description = "Cuenta bloqueada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Cuenta desactivada")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest) {
        try {
            AuthResponse authResponse = authService.authenticate(request, httpRequest);
            log.info("Login exitoso para usuario: {}", request.getUsername());
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Login exitoso"));
        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para usuario: {}", request.getUsername());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Credenciales inválidas", null));
        } catch (LockedException e) {
            log.warn("Intento de login en cuenta bloqueada: {}", request.getUsername());
            return ResponseEntity.status(423)
                    .body(ApiResponse.error(423, "Cuenta bloqueada por múltiples intentos fallidos", null));
        } catch (DisabledException e) {
            log.warn("Intento de login en cuenta desactivada: {}", request.getUsername());
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "Cuenta desactivada", null));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renueva el access token usando el refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse authResponse = authService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Token renovado exitosamente"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Refresh token inválido o expirado", null));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida los tokens del usuario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout exitoso")
    })
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) LogoutRequest request) {
        try {
            var currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser != null) {
                authService.logout(currentUser.getUsername());
                log.info("Logout exitoso para usuario: {}", currentUser.getUsername());
            }
            return ResponseEntity.ok(ApiResponse.success(null, "Logout exitoso"));
        } catch (Exception e) {
            log.error("Error durante logout", e);
            return ResponseEntity.ok(ApiResponse.success(null, "Logout completado"));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Información del usuario actual", description = "Obtiene la información del usuario autenticado")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Información obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser() {
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Usuario no autenticado", null));
        }

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(currentUser.getIdUsuario())
                .username(currentUser.getUsername())
                .nombre(currentUser.getNombre())
                .email(currentUser.getEmail())
                .tipoUsuario(currentUser.getTipoUsuario().name())
                .tipoUsuarioDisplayName(currentUser.getTipoUsuario().getDisplayName())
                .empresaId(currentUser.getEmpresa().getIdEmpresa())
                .empresaNombre(currentUser.getEmpresa().getNombre())
                .permissions(currentUser.getTipoUsuario().getPermissions())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userInfo, "Información del usuario obtenida exitosamente"));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida si el token actual es válido")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token válido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido")
    })
    public ResponseEntity<ApiResponse<Boolean>> validateToken() {
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        boolean isValid = currentUser != null;
        return ResponseEntity.ok(ApiResponse.success(isValid, isValid ? "Token válido" : "Token inválido"));
    }
}