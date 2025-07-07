package com.example.demo.entity;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.repository.CategoryRepository;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_user")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String socialKey;

    private String nickname;
    private String email;
    private LocalDate birthDate;
    private String region;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Post> posts;

    // 유저가 선호하는 카테고리
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCategory> userCategories = new ArrayList<>();

    @Builder
    public UserEntity(String socialKey, String nickname, String email, LocalDate birthDate, String region, List<Long> categoryIds, List<Post> posts) {
        this.socialKey = socialKey;
        this.nickname = nickname;
        this.email = email;
        this.birthDate = birthDate;
        this.region = region;
//        this.categoryIds = categoryIds;
        this.posts = posts;
    }

    public UserEntity(String email) {
        this.email = email;
    }

    public UserResponseDTO toUserResponseDTO() {
        return UserResponseDTO.builder()
                .email(this.email)
                .nickname(this.nickname)
                .birthDate(this.birthDate)
                .region(this.region)
                .categoryIds(this.userCategories.stream()
                        .map(uc -> uc.getCategory().getCategoryId())
                        .collect(Collectors.toList()))
                .build();
    }

    public UserResponseDTO toUserLoginResponseDTO() {
        return UserResponseDTO.builder()
                .nickname(this.nickname)
                .email(this.email)
                .birthDate(this.birthDate)
                .region(this.region)
                .categoryIds(this.userCategories.stream()
                        .map(uc -> uc.getCategory().getCategoryId())
                        .collect(Collectors.toList()))
                .build();
    }

    public UserEntity updateByDto(UserUpdateDTO userUpdateDTO) {
        this.birthDate = LocalDate.parse(userUpdateDTO.getBirthDate());
        this.nickname = userUpdateDTO.getNickName();
        this.region = userUpdateDTO.getRegion();
        return this;
    }
}