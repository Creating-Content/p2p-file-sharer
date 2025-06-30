
// src/main/java/com/filesharer/securefilesharer/service/LocalFileStorageServiceImpl.java
package com.filesharer.securefilesharer.service;

import com.filesharer.securefilesharer.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    // Directory to store uploaded files, configurable via application.properties
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct // This method runs after dependency injection is complete
    public void init() {
        // Resolve the upload directory path and normalize it
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // Create the directory if it does not exist
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            // Throw a runtime exception if the directory cannot be created
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored: " + fileStorageLocation, ex);
        }
    }

    @Override
    public String saveFile(MultipartFile file) {
        // Sanitize original file name and extract extension
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        // Generate a unique file name using UUID to prevent collisions
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Check for invalid path characters
            if (storedFileName.contains("..")) {
                throw new IllegalArgumentException("Filename contains invalid path sequence: " + storedFileName);
            }

            // Resolve target location and copy the file
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return storedFileName; // Return the unique stored name
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            // Resolve the file path and normalize it
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            // Create a URL resource from the file path
            Resource resource = new UrlResource(filePath.toUri());
            // Check if the resource exists and is readable
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + fileName, ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            // Resolve the file path and attempt to delete it
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath); // Deletes only if the file exists
        } catch (IOException ex) {
            // Log the exception but do not rethrow, as it might mean the file was already gone
            System.err.println("Could not delete file " + fileName + ": " + ex.getMessage());
            // In a real application, you might use a proper logger (e.g., SLF4J)
        }
    }
}
