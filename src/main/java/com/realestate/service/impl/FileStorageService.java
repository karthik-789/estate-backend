package com.realestate.service.impl;

import com.realestate.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String store(Long propertyId, MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("Image file cannot be empty.");
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')).toLowerCase() : "";
        if (!Set.of(".jpg", ".jpeg", ".png", ".webp").contains(extension)) {
            throw new BadRequestException("Only JPG, PNG, and WEBP images are supported.");
        }
        try {
            Path directory = root.resolve("properties").resolve(String.valueOf(propertyId));
            Files.createDirectories(directory);
            String filename = UUID.randomUUID() + extension;
            Files.copy(file.getInputStream(), directory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/properties/" + propertyId + "/" + filename;
        } catch (IOException exception) {
            throw new BadRequestException("Unable to store image.");
        }
    }
}

