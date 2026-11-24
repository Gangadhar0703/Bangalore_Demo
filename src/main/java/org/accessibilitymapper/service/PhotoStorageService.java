package org.accessibilitymapper.service;

import org.accessibilitymapper.config.AppProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class PhotoStorageService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final AppProperties properties;

    public PhotoStorageService(AppProperties properties) {
        this.properties = properties;
    }

    public String store(MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) return null;
        if (!ALLOWED_TYPES.contains(photo.getContentType())) {
            throw new IllegalArgumentException("Upload a JPG, PNG, or WebP image.");
        }
        String extension = switch (photo.getContentType()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> ".webp";
        };
        Path directory = properties.uploadPath();
        Files.createDirectories(directory);
        String filename = UUID.randomUUID() + extension;
        photo.transferTo(directory.resolve(filename));
        return "/uploads/" + filename;
    }
}
