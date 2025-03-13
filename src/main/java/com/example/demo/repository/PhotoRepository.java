package com.example.demo.repository;

import com.example.demo.entity.Photo;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}

