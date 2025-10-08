package com.djasoft.mozaico.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private Access access = new Access();
    private Refresh refresh = new Refresh();

    @Data
    public static class Access {
        private Long expiration = 900000L; // 15 minutos
    }

    @Data
    public static class Refresh {
        private Long expiration = 604800000L; // 7 días
    }
}