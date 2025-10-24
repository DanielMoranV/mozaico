package com.djasoft.mozaico.config;

import com.djasoft.mozaico.services.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/uploads/images/products/**").permitAll()
                        .requestMatchers("/api/v1/comprobantes/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll() // Health checks

                        // Endpoints públicos para la carta de productos (sin autenticación)
                        .requestMatchers("/api/v1/productos/public/**").permitAll()
                        .requestMatchers("/api/v1/carta-qr/public/**").permitAll()

                        // Endpoints que requieren autenticación específica
                        .requestMatchers("/api/v1/usuarios/**")
                        .hasAnyAuthority("ROLE_MANAGE_USERS", "ROLE_ALL_PERMISSIONS")
                        .requestMatchers("/api/v1/pagos/**")
                        .hasAnyAuthority("ROLE_MANAGE_PAYMENTS", "ROLE_ALL_PERMISSIONS")
                        .requestMatchers("/api/v1/pedidos/**")
                        .hasAnyAuthority("ROLE_MANAGE_ORDERS", "ROLE_VIEW_ORDERS", "ROLE_ALL_PERMISSIONS")
                        .requestMatchers("/api/v1/reservas/**")
                        .hasAnyAuthority("ROLE_MANAGE_RESERVATIONS", "ROLE_VIEW_RESERVATIONS", "ROLE_ALL_PERMISSIONS")

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Usar allowedOriginPatterns para permitir acceso desde la red local y dominios de producción
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://0.0.0.0:*",
                "http://192.168.1.*:*",  // Permitir cualquier dispositivo en la red local
                "https://mozaico.djasoft.net.pe",  // Frontend de producción
                "https://*.djasoft.net.pe"  // Cualquier subdominio de djasoft.net.pe
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Permitir todas las cabeceras
        configuration.setAllowCredentials(true); // Permitir credenciales (cookies, etc.)
        configuration.setMaxAge(3600L); // Cache preflight request for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplicar esta configuración a todas las rutas
        return source;
    }
}
