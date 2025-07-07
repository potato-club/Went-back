package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}