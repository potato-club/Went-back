package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_user_category")
public class UserCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_category_id")
    private Long userCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public UserCategory(UserEntity user, Category category) {
        this.user = user;
        this.category = category;
    }

    public void assignUser(UserEntity user) {
        this.user = user;
    }

    public void assignCategory(Category category) {
        this.category = category;
    }

}
