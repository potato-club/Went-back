package com.example.demo.dto;

public class CategoryDTO {

    private Long categoryId; // 반드시 필드명 일치

    private String name;
    private String koName;

    // Getter/Setter
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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