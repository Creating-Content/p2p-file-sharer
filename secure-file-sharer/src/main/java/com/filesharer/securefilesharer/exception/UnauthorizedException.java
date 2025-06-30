
// src/main/java/com/filesharer/securefilesharer/exception/UnauthorizedException.java
package com.filesharer.securefilesharer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // Or HttpStatus.FORBIDDEN depending on the context
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
