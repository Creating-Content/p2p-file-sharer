// src/main/java/com/filesharer/securefilesharer/dto/ShareLinkResponse.java
package com.filesharer.securefilesharer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // IMPORTANT: Keep this annotation
public class ShareLinkResponse {
    private String shareUrl;
    private String uniqueToken;
    private String originalFileName;
    private LocalDateTime expiresAt;
    private Integer maxDownloads;
    private Integer currentDownloads;
    private String message;
    private Long fileId; // IMPORTANT: Added this field
}
