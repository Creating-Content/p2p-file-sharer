// src/main/java/com/filesharer/securefilesharer/repository/UserRepository.java
package com.filesharer.securefilesharer.repository;

import com.filesharer.securefilesharer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // IMPORTANT: Add this line
}
