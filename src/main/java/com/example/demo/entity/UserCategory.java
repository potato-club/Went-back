package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class UserCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCategoryId;
}
