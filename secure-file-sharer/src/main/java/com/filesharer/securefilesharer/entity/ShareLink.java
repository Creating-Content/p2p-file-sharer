// src/main/java/com/filesharer/securefilesharer/entity/ShareLink.java
package com.filesharer.securefilesharer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "share_links")
@Data // Generates getters, setters, equals, hashCode, toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder // Provides a builder pattern for creating instances
public class ShareLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming auto-incrementing ID
    private Long id;

    @Column(nullable = false, unique = true)
    private String uniqueToken; // The UUID generated for sharing

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private File file; // The file being shared

    @ManyToOne(fetch = FetchType.LAZY) // User who created the share link
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt; // Optional: when the link expires

    private String password; // Optional: password for the link (hashed in real app)

    private Integer maxDownloads; // Optional: maximum number of downloads

    @Column(nullable = false)
    private Integer downloadCount = 0; // Current download count, initialized to 0

    // CascadeType.ALL is often too broad; PERSIST and MERGE are usually sufficient.
    // For simplicity with Lombok's @Data, explicit getters/setters are not written
    // as Lombok handles them. Just ensure the field names are correct.
}
