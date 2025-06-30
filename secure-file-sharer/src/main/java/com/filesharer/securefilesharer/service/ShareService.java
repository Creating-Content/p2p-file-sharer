// src/main/java/com/filesharer/securefilesharer/service/ShareService.java
package com.filesharer.securefilesharer.service;

import com.filesharer.securefilesharer.dto.ShareLinkResponse;
import com.filesharer.securefilesharer.entity.File;
import com.filesharer.securefilesharer.entity.ShareLink;
import com.filesharer.securefilesharer.entity.User;
import com.filesharer.securefilesharer.exception.ResourceNotFoundException;
import com.filesharer.securefilesharer.exception.ShareLinkExpiredException; // Now this import should work
import com.filesharer.securefilesharer.exception.ShareLinkMaxDownloadsExceededException; // Now this import should work
import com.filesharer.securefilesharer.exception.UnauthorizedException;
import com.filesharer.securefilesharer.repository.FileRepository;
import com.filesharer.securefilesharer.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareLinkRepository shareLinkRepository;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ShareLinkResponse generateShareLink(Long fileId, User creator, Integer expiryMinutes, String password, Integer maxDownloads) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + fileId));

        String uniqueToken = UUID.randomUUID().toString();

        ShareLink shareLink = new ShareLink();
        shareLink.setUniqueToken(uniqueToken);
        shareLink.setFile(file);
        shareLink.setCreatedBy(creator);
        shareLink.setCreatedAt(LocalDateTime.now());
        shareLink.setDownloadCount(0); // This setter should now exist via Lombok @Data

        if (expiryMinutes != null && expiryMinutes > 0) {
            shareLink.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        }
        if (password != null && !password.isEmpty()) {
            shareLink.setPassword(password); // This setter should now exist via Lombok @Data
        }
        if (maxDownloads != null && maxDownloads > 0) {
            shareLink.setMaxDownloads(maxDownloads); // This setter should now exist via Lombok @Data
        }

        ShareLink savedShareLink = shareLinkRepository.save(shareLink);

        String fullShareUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .port(5173) // IMPORTANT: Change this to your frontend's actual running port
                .path("/receive")
                .queryParam("code", savedShareLink.getUniqueToken())
                .toUriString();

        return ShareLinkResponse.builder()
                .uniqueToken(savedShareLink.getUniqueToken())
                .shareUrl(fullShareUrl)
                .fileId(savedShareLink.getFile().getId()) // This should now work as fileId is in ShareLinkResponse DTO
                .originalFileName(savedShareLink.getFile().getOriginalName())
                .expiresAt(savedShareLink.getExpiresAt())
                .maxDownloads(savedShareLink.getMaxDownloads())
                .currentDownloads(savedShareLink.getDownloadCount())
                .message("Share link generated successfully.")
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<ShareLink> getShareLinkByUniqueToken(String uniqueToken) {
        return shareLinkRepository.findByUniqueToken(uniqueToken);
    }

    @Transactional(readOnly = true)
    public ShareLink getShareLinkByToken(String uniqueToken) {
        return shareLinkRepository.findByUniqueToken(uniqueToken)
                .orElseThrow(() -> new ResourceNotFoundException("Share link not found: " + uniqueToken));
    }

    @Transactional
    public Resource accessSharedFile(String uniqueToken) {
        ShareLink shareLink = getShareLinkByToken(uniqueToken);

        if (shareLink.getExpiresAt() != null && shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ShareLinkExpiredException("Share link has expired.");
        }
        if (shareLink.getMaxDownloads() != null && shareLink.getDownloadCount() >= shareLink.getMaxDownloads()) {
            throw new ShareLinkMaxDownloadsExceededException("Maximum downloads for this link exceeded.");
        }

        shareLink.setDownloadCount(shareLink.getDownloadCount() + 1);
        shareLinkRepository.save(shareLink);

        File file = shareLink.getFile();
        if (file == null) {
            throw new ResourceNotFoundException("Associated file not found for share link: " + uniqueToken);
        }
        return fileStorageService.loadFileAsResource(file.getStoredName());
    }

    @Transactional
    public void revokeShareLink(String uniqueToken, User currentUser) {
        ShareLink shareLink = getShareLinkByToken(uniqueToken);

        if (!shareLink.getCreatedBy().getId().equals(currentUser.getId()) &&
            !shareLink.getFile().getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to revoke this share link.");
        }
        shareLinkRepository.delete(shareLink);
    }

    @Transactional(readOnly = true)
    public ShareLinkResponse getShareLinkDetails(String uniqueToken, User currentUser) {
        ShareLink shareLink = getShareLinkByToken(uniqueToken);

        if (!shareLink.getCreatedBy().getId().equals(currentUser.getId()) &&
            !shareLink.getFile().getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view details of this share link.");
        }

        File file = shareLink.getFile();
        return ShareLinkResponse.builder()
                .uniqueToken(shareLink.getUniqueToken())
                .shareUrl(ServletUriComponentsBuilder.fromCurrentContextPath()
                                .port(5173) // IMPORTANT: Adjust port
                                .path("/receive")
                                .queryParam("code", shareLink.getUniqueToken())
                                .toUriString())
                .fileId(file.getId())
                .originalFileName(file.getOriginalName())
                .expiresAt(shareLink.getExpiresAt())
                .maxDownloads(shareLink.getMaxDownloads())
                .currentDownloads(shareLink.getDownloadCount())
                .message("Share link details fetched successfully.")
                .build();
    }
}
