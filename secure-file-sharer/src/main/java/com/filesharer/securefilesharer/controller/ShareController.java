// src/main/java/com/filesharer/securefilesharer/controller/ShareController.java
package com.filesharer.securefilesharer.controller;

import com.filesharer.securefilesharer.dto.ShareLinkRequest;
import com.filesharer.securefilesharer.dto.ShareLinkResponse;
import com.filesharer.securefilesharer.entity.ShareLink;
import com.filesharer.securefilesharer.entity.User;
import com.filesharer.securefilesharer.exception.ResourceNotFoundException;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final FileService fileService;
    private final UserService userService;

    // Helper method to retrieve the current authenticated User entity
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }

    /**
     * Endpoint for public access to download a shared file using its unique token.
     * This endpoint does NOT require authentication.
     * @param uniqueToken The unique string token (UUID) of the share link.
     * @return ResponseEntity with the file resource.
     */
    @GetMapping("/download/{uniqueToken}")
    public ResponseEntity<Resource> downloadSharedFile(@PathVariable String uniqueToken) {
        Resource resource = shareService.accessSharedFile(uniqueToken);
        ShareLink shareLink = shareService.getShareLinkByToken(uniqueToken); // Now this method exists

        // Get file metadata to determine content type and original file name
        com.filesharer.securefilesharer.entity.File fileMetadata = shareLink.getFile();
        String contentType = fileMetadata.getMimeType() != null ? fileMetadata.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String originalFileName = fileMetadata.getOriginalName();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(resource);
    }

    /**
     * Endpoint to revoke (delete) a share link.
     * Accessible only by the file owner (or the user who created the share link).
     * @param uniqueToken The unique string token of the share link to revoke.
     * @return ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/revoke/{uniqueToken}")
    @PreAuthorize("hasRole('ROLE_USER')") // Only authenticated users can revoke links
    public ResponseEntity<Void> revokeShareLink(@PathVariable String uniqueToken) {
        User currentUser = getCurrentUser();
        shareService.revokeShareLink(uniqueToken, currentUser); // Now this method exists
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to get details of a specific share link.
     * Accessible only by the file owner.
     * @param uniqueToken The unique string token of the share link.
     * @return ResponseEntity with ShareLink details.
     */
    @GetMapping("/details/{uniqueToken}")
    @PreAuthorize("hasRole('ROLE_USER')") // Only authenticated users can view link details
    public ResponseEntity<ShareLinkResponse> getShareLinkDetails(@PathVariable String uniqueToken) {
        User currentUser = getCurrentUser();
        ShareLinkResponse response = shareService.getShareLinkDetails(uniqueToken, currentUser); // Now this method exists
        return ResponseEntity.ok(response);
    }
}
