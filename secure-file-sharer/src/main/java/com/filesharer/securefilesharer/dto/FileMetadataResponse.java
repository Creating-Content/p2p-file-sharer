
// src/main/java/com/filesharer/securefilesharer/dto/FileMetadataResponse.java
package com.filesharer.securefilesharer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataResponse {
    private Long id;
    private String originalName;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadTimestamp;
    private Integer downloadCount;
    private String ownerUsername;
    private String downloadUri; // Added for convenience in UI
}
