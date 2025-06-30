
// src/main/java/com/filesharer/securefilesharer/dto/AuthResponse.java
package com.filesharer.securefilesharer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String message;
}
