// src/main/java/com/filesharer/securefilesharer/entity/File.java
package com.filesharer.securefilesharer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CHANGED: FetchType.LAZY to FetchType.EAGER to prevent LazyInitializationException
    // when accessing owner details outside of a session, especially for DTO conversion.
    @ManyToOne(fetch = FetchType.EAGER) // Now eager-loads the owner when a File is fetched
    @JoinColumn(name = "owner_id", nullable = false) // Foreign key to User
    private User owner;

    @Column(nullable = false)
    private String originalName; // Original file name uploaded by user

    @Column(nullable = false, unique = true)
    private String storedName; // Unique name for storage on disk/S3

    private String mimeType; // e.g., "image/jpeg", "application/pdf"

    private Long sizeBytes; // File size in bytes

    private LocalDateTime uploadTimestamp; // When the file was uploaded

    private Integer downloadCount; // How many times this file has been downloaded

    @PrePersist
    protected void onCreate() {
        uploadTimestamp = LocalDateTime.now();
        downloadCount = 0; // Initialize download count
    }
}
