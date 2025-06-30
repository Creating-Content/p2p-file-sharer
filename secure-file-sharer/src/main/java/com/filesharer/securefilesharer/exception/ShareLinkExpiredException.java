// src/main/java/com/filesharer/securefilesharer/exception/ShareLinkExpiredException.java
package com.filesharer.securefilesharer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Or HttpStatus.GONE (410)
public class ShareLinkExpiredException extends RuntimeException {
    public ShareLinkExpiredException(String message) {
        super(message);
    }

    public ShareLinkExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
