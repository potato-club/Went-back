package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySocialKey(String socialKey);
    List<UserEntity> findBySocialKeyAndEmail(String socialKey, String email);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsBySocialKey(String socialKey);
}
