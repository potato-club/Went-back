package com.example.demo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tb_category")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키

    @Column(nullable = false, unique = true)
    private String name; // Enum 코드 (예: "MOVIE", "BOOK" 등)

    @Column(nullable = false)
    private String koName; // 한글명 (예: "영화", "책" 등)

    public Category() {}

    public Category(String name, String koName) {
        this.name = name;
        this.koName = koName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKoName() {
        return koName;
    }

    public void setKoName(String koName) {
        this.koName = koName;
    }
}