
// src/main/java/com/filesharer/securefilesharer/repository/FileRepository.java
package com.filesharer.securefilesharer.repository;

import com.filesharer.securefilesharer.entity.File;
import com.filesharer.securefilesharer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwner(User owner); // Find all files belonging to a specific user
    Optional<File> findByIdAndOwner(Long id, User owner); // Find a file by ID and its owner
}
