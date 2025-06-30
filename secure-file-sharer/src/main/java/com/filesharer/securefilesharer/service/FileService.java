// src/main/java/com/filesharer/securefilesharer/service/FileService.java
package com.filesharer.securefilesharer.service;

import com.filesharer.securefilesharer.dto.FileMetadataResponse;
import com.filesharer.securefilesharer.dto.FileUploadResponse;
import com.filesharer.securefilesharer.entity.File;
import com.filesharer.securefilesharer.entity.User;
import com.filesharer.securefilesharer.exception.ResourceNotFoundException;
import com.filesharer.securefilesharer.repository.FileRepository;
import com.filesharer.securefilesharer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository; // To get User entity

    @Transactional // Ensures atomicity of file storage and database update
    public FileUploadResponse uploadFile(MultipartFile multipartFile, User user) {
        // Save file to local storage (or cloud storage if you implement that)
        String storedFileName = fileStorageService.saveFile(multipartFile);

        // Create and save file metadata in the database
        File file = new File();
        file.setOriginalName(multipartFile.getOriginalFilename());
        file.setStoredName(storedFileName);
        file.setMimeType(multipartFile.getContentType());
        file.setSizeBytes(multipartFile.getSize());
        file.setOwner(user); // Set the owner of the file

        File savedFile = fileRepository.save(file);

        // Construct the direct download URI for the uploaded file
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(String.valueOf(savedFile.getId()))
                .toUriString();

        return new FileUploadResponse(
                savedFile.getId(), // Return file ID for share link generation
                savedFile.getOriginalName(),
                fileDownloadUri,
                savedFile.getMimeType(),
                savedFile.getSizeBytes()
        );
    }

    public Resource downloadFile(Long fileId, User user) {
        // Retrieve the file from the database, ensuring the user is the owner
        File file = fileRepository.findByIdAndOwner(fileId, user)
                .orElseThrow(() -> new ResourceNotFoundException("File not found or not owned by user: " + fileId));

        // Increment download count and save (transactional context for this update)
        file.setDownloadCount(file.getDownloadCount() + 1);
        fileRepository.save(file); // Persist the updated download count

        // Load the actual file from storage as a Spring Resource
        return fileStorageService.loadFileAsResource(file.getStoredName());
    }

    @Transactional // Ensures atomicity of file deletion from storage and database
    public void deleteFile(Long fileId, User user) {
        // Retrieve the file, ensuring the user is the owner
        File file = fileRepository.findByIdAndOwner(fileId, user)
                .orElseThrow(() -> new ResourceNotFoundException("File not found or not owned by user: " + fileId));

        // Delete the actual file from storage
        fileStorageService.deleteFile(file.getStoredName());
        // Delete the file metadata from the database
        fileRepository.delete(file);
    }

    // ADDED: @Transactional to ensure owner is accessible within the session
    @Transactional(readOnly = true) // readOnly = true for optimization
    public List<FileMetadataResponse> getFilesByOwner(User owner) {
        List<File> files = fileRepository.findByOwner(owner);
        // Convert File entities to FileMetadataResponse DTOs
        // Because owner is now EAGER fetched in File.java, and this method is @Transactional,
        // accessing file.getOwner().getUsername() should be safe.
        return files.stream()
                .map(file -> new FileMetadataResponse(
                        file.getId(),
                        file.getOriginalName(),
                        file.getMimeType(),
                        file.getSizeBytes(),
                        file.getUploadTimestamp(),
                        file.getDownloadCount(),
                        file.getOwner().getUsername(), // Accessing owner is now safe
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/files/download/")
                                .path(String.valueOf(file.getId()))
                                .toUriString()
                ))
                .collect(Collectors.toList());
    }

    // ADDED: @Transactional to ensure owner is accessible within the session
    @Transactional(readOnly = true) // readOnly = true for optimization
    // Helper method to get file metadata by ID (for internal use, e.g., in ShareController)
    public FileMetadataResponse getFileMetadata(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File metadata not found for id: " + fileId));
        // Accessing owner is now safe due to EAGER fetch and @Transactional
        return new FileMetadataResponse(
                file.getId(),
                file.getOriginalName(),
                file.getMimeType(),
                file.getSizeBytes(),
                file.getUploadTimestamp(),
                file.getDownloadCount(),
                file.getOwner().getUsername(), // Accessing owner is now safe
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/files/download/")
                        .path(String.valueOf(file.getId()))
                        .toUriString()
        );
    }

    // Method to get file entity by ID and owner's username
    public Optional<File> getFileByIdAndOwner(Long fileId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username)); // Should not happen for authenticated user
        return fileRepository.findByIdAndOwner(fileId, owner);
    }
}
