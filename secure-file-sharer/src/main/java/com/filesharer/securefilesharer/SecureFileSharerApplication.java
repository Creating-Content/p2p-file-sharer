// src/main/java/com/filesharer/securefilesharer/SecureFileSharerApplication.java
package com.filesharer.securefilesharer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecureFileSharerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileSharerApplication.class, args);
	}

	// Bean for password encoding, now correctly placed in the main application class
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
