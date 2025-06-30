// src/main/java/com/filesharer/securefilesharer/exception/ShareLinkMaxDownloadsExceededException.java
package com.filesharer.securefilesharer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Or HttpStatus.GONE (410)
public class ShareLinkMaxDownloadsExceededException extends RuntimeException {
    public ShareLinkMaxDownloadsExceededException(String message) {
        super(message);
    }

    public ShareLinkMaxDownloadsExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
