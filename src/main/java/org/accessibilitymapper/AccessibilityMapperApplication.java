package org.accessibilitymapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AccessibilityMapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessibilityMapperApplication.class, args);
    }
}
