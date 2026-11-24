package org.accessibilitymapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String uploadDir, boolean seedDemoData,
                            String adminEmail, String adminPassword,
                            String memberEmail, String memberPassword) {
    public Path uploadPath() {
        return Path.of(uploadDir).toAbsolutePath().normalize();
    }
}
