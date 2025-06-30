
// src/main/java/com/filesharer/securefilesharer/dto/ShareLinkRequest.java
package com.filesharer.securefilesharer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Required for JSON deserialization
@AllArgsConstructor // Useful for creating instances easily
public class ShareLinkRequest {
    @NotNull(message = "File ID cannot be null")
    private Long fileId;
    private Integer expiryMinutes; // Optional: How long the link is valid in minutes
    private String password; // Optional: Password for the share link
    private Integer maxDownloads; // Optional: Max download limit for the link (1 for one-time use)
}
