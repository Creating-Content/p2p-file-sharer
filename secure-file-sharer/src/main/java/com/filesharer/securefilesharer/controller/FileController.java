// src/main/java/com/filesharer/securefilesharer/controller/FileController.java
package com.filesharer.securefilesharer.controller;

import com.filesharer.securefilesharer.dto.FileMetadataResponse;
import com.filesharer.securefilesharer.dto.FileUploadResponse;
import com.filesharer.securefilesharer.dto.ShareLinkRequest;
import com.filesharer.securefilesharer.dto.ShareLinkResponse;
import com.filesharer.securefilesharer.entity.User;
import com.filesharer.securefilesharer.exception.UnauthorizedException;
import com.filesharer.securefilesharer.service.FileService;
import com.filesharer.securefilesharer.service.ShareService;
import com.filesharer.securefilesharer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserService userService;
    private final ShareService shareService;

    // Helper method to retrieve the current authenticated User entity
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from SecurityContext
        return userService.getUserByUsername(username); // Retrieve User entity by username
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_USER')") // Only users with 'ROLE_USER' can upload files
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        User currentUser = getCurrentUser();
        FileUploadResponse response = fileService.uploadFile(file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')") // Only users with 'ROLE_USER' can view their files
    public ResponseEntity<List<FileMetadataResponse>> getMyFiles() {
        User currentUser = getCurrentUser();
        List<FileMetadataResponse> files = fileService.getFilesByOwner(currentUser);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{fileId}")
    @PreAuthorize("hasRole('ROLE_USER')") // Only file owner can download via this direct link
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        User currentUser = getCurrentUser();
        Resource resource = fileService.downloadFile(fileId, currentUser);

        // Get file metadata to determine content type and original file name
        FileMetadataResponse fileMetadata = fileService.getFileMetadata(fileId);
        String contentType = fileMetadata.getMimeType() != null ? fileMetadata.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String originalFileName = fileMetadata.getOriginalName();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasRole('ROLE_USER')") // Only file owner can delete their files
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        User currentUser = getCurrentUser();
        fileService.deleteFile(fileId, currentUser);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/{fileId}/share")
    @PreAuthorize("hasRole('ROLE_USER')") // Authenticated users can generate share links for their files
    public ResponseEntity<ShareLinkResponse> generateShareLink(@PathVariable Long fileId,
                                                                @RequestBody(required = false) ShareLinkRequest request) {
        User currentUser = getCurrentUser();
        // Verify user owns the file before allowing link generation
        fileService.getFileByIdAndOwner(fileId, currentUser.getUsername())
                .orElseThrow(() -> new UnauthorizedException("File not found or not owned by current user: " + fileId));

        ShareLinkResponse response = shareService.generateShareLink(
                fileId,
                currentUser,
                request != null ? request.getExpiryMinutes() : null,
                request != null ? request.getPassword() : null,
                request != null ? request.getMaxDownloads() : null
        );
        // Add a print statement here to see what's being returned by shareService
        System.out.println("FileController returning ShareLinkResponse: " + response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
