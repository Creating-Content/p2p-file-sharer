// src/main/java/com/filesharer/securefilesharer/repository/ShareLinkRepository.java
package com.filesharer.securefilesharer.repository;

import com.filesharer.securefilesharer.entity.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {
    Optional<ShareLink> findByUniqueToken(String uniqueToken); // Find a share link by its unique token
    Optional<ShareLink> findByFileIdAndCreatedByAndExpiresAtIsNullAndMaxDownloadsIsNull(Long fileId, com.filesharer.securefilesharer.entity.User createdBy); // Find an existing permanent link for a file by a specific user
}
