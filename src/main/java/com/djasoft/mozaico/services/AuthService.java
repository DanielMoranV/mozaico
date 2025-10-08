package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.UsuarioRepository;
import com.djasoft.mozaico.web.dtos.auth.AuthRequest;
import com.djasoft.mozaico.web.dtos.auth.AuthResponse;
import com.djasoft.mozaico.web.dtos.auth.RefreshTokenRequest;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService;

    @Transactional
    public AuthResponse authenticate(AuthRequest request, HttpServletRequest httpRequest) {
        try {
            // Buscar usuario para validaciones adicionales
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

            // Verificar si la cuenta está bloqueada
            if (!usuario.isAccountNonLocked()) {
                throw new LockedException("Cuenta bloqueada por múltiples intentos fallidos");
            }

            // Verificar si la cuenta está activa
            if (!usuario.isEnabled()) {
                throw new DisabledException("Cuenta desactivada");
            }

            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Si llegamos aquí, la autenticación fue exitosa
            Usuario authenticatedUser = (Usuario) authentication.getPrincipal();

            // Resetear intentos fallidos y actualizar último acceso
            authenticatedUser.resetFailedAttempts();
            authenticatedUser.updateLastAccess(getClientIpAddress(httpRequest));

            // Generar tokens
            String accessToken = jwtService.generateToken(authenticatedUser);
            String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

            // Guardar el último token JWT
            authenticatedUser.setUltimoTokenJwt(accessToken);
            usuarioRepository.save(authenticatedUser);

            // Auditar login exitoso
            auditService.logUserAction(
                    authenticatedUser,
                    authenticatedUser.getEmpresa(),
                    "LOGIN",
                    "Login exitoso",
                    getClientIpAddress(httpRequest),
                    httpRequest.getHeader("User-Agent"));

            log.info("Usuario autenticado exitosamente: {} - Empresa: {}",
                    authenticatedUser.getUsername(),
                    authenticatedUser.getEmpresa().getIdEmpresa());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(15 * 60) // 15 minutos en segundos
                    .user(AuthResponse.UserInfo.builder()
                            .id(authenticatedUser.getIdUsuario())
                            .username(authenticatedUser.getUsername())
                            .nombre(authenticatedUser.getNombre())
                            .email(authenticatedUser.getEmail())
                            .tipoUsuario(authenticatedUser.getTipoUsuario().name())
                            .tipoUsuarioDisplayName(authenticatedUser.getTipoUsuario().getDisplayName())
                            .empresaId(authenticatedUser.getEmpresa().getIdEmpresa())
                            .empresaNombre(authenticatedUser.getEmpresa().getNombre())
                            .permissions(authenticatedUser.getTipoUsuario().getPermissions())
                            .build())
                    .build();

        } catch (BadCredentialsException e) {
            // Incrementar intentos fallidos y auditar
            usuarioRepository.findByUsername(request.getUsername())
                    .ifPresent(user -> {
                        user.incrementFailedAttempts();
                        usuarioRepository.save(user);

                        // Auditar intento fallido
                        auditService.logUserAction(
                                user,
                                user.getEmpresa(),
                                "LOGIN_FAILED",
                                "Intento de login fallido - Credenciales inválidas",
                                getClientIpAddress(httpRequest),
                                httpRequest.getHeader("User-Agent"));

                        log.warn("Intento de login fallido para usuario: {} - Intentos: {}",
                                user.getUsername(), user.getIntentosFallidos());
                    });
            throw new BadCredentialsException("Credenciales inválidas");
        } catch (AuthenticationException e) {
            log.error("Error de autenticación para usuario: {}", request.getUsername(), e);
            throw e;
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Token de refresh inválido");
        }

        String username = jwtService.extractUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new BadCredentialsException("Token de refresh inválido o expirado");
        }

        // Generar nuevo access token
        String newAccessToken = jwtService.generateToken(usuario);
        usuario.setUltimoTokenJwt(newAccessToken);
        usuarioRepository.save(usuario);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Mantener el mismo refresh token
                .tokenType("Bearer")
                .expiresIn(15 * 60)
                .user(AuthResponse.UserInfo.builder()
                        .id(usuario.getIdUsuario())
                        .username(usuario.getUsername())
                        .nombre(usuario.getNombre())
                        .email(usuario.getEmail())
                        .tipoUsuario(usuario.getTipoUsuario().name())
                        .tipoUsuarioDisplayName(usuario.getTipoUsuario().getDisplayName())
                        .empresaId(usuario.getEmpresa().getIdEmpresa())
                        .empresaNombre(usuario.getEmpresa().getNombre())
                        .permissions(usuario.getTipoUsuario().getPermissions())
                        .build())
                .build();
    }

    @Transactional
    public void logout(String username) {
        usuarioRepository.findByUsername(username)
                .ifPresent(usuario -> {
                    usuario.invalidateTokens();
                    usuarioRepository.save(usuario);

                    // Auditar logout
                    auditService.logUserAction(
                            usuario,
                            usuario.getEmpresa(),
                            "LOGOUT",
                            "Usuario cerró sesión",
                            "system",
                            "system");

                    log.info("Usuario deslogueado: {}", username);
                });
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}