package com.djasoft.mozaico.services;

import com.djasoft.mozaico.config.JwtProperties;
import com.djasoft.mozaico.domain.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        if (userDetails instanceof Usuario usuario) {
            extraClaims.put("userId", usuario.getIdUsuario());
            extraClaims.put("empresaId", usuario.getEmpresa().getIdEmpresa());
            extraClaims.put("tipoUsuario", usuario.getTipoUsuario().name());
            extraClaims.put("tokenVersion", usuario.getTokenVersion());
            extraClaims.put("permissions", usuario.getTipoUsuario().getPermissions());
        }

        return buildToken(extraClaims, userDetails, jwtProperties.getAccess().getExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof Usuario usuario) {
            extraClaims.put("userId", usuario.getIdUsuario());
            extraClaims.put("tokenVersion", usuario.getTokenVersion());
            extraClaims.put("type", "refresh");
        }

        return buildToken(extraClaims, userDetails, jwtProperties.getRefresh().getExpiration());
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, Usuario usuario) {
        try {
            final String username = extractUsername(token);
            final Long tokenVersion = extractClaim(token, claims -> claims.get("tokenVersion", Long.class));

            return username.equals(usuario.getUsername())
                && !isTokenExpired(token)
                && tokenVersion.equals(usuario.getTokenVersion());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public Long extractEmpresaId(String token) {
        return extractClaim(token, claims -> claims.get("empresaId", Long.class));
    }

    public String extractTipoUsuario(String token) {
        return extractClaim(token, claims -> claims.get("tipoUsuario", String.class));
    }

    public Long extractTokenVersion(String token) {
        return extractClaim(token, claims -> claims.get("tokenVersion", Long.class));
    }

    public boolean isRefreshToken(String token) {
        try {
            String type = extractClaim(token, claims -> claims.get("type", String.class));
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
}