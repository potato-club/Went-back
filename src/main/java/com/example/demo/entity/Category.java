package com.example.demo.entity;

import com.example.demo.entity.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@Entity
@Getter
@Table(name = "tb_category")
public class Category implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long CategoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;
}