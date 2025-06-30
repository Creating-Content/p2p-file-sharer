
// src/main/java/com/filesharer/securefilesharer/dto/FileUploadResponse.java
package com.filesharer.securefilesharer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Long fileId; // Added to return file ID for link generation
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
