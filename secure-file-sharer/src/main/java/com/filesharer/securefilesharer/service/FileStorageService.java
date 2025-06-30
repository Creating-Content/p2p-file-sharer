
// src/main/java/com/filesharer/securefilesharer/service/FileStorageService.java
package com.filesharer.securefilesharer.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

// Interface for file storage operations (local, S3, etc.)
public interface FileStorageService {
    String saveFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
}
