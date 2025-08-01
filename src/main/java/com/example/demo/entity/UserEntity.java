package com.example.demo.entity;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.request.UserUpdateDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> likes = new ArrayList<>();

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
                .profileImageUrl(this.profileImageUrl)
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
        this.nickname = userUpdateDTO.getNickname();
        this.region = userUpdateDTO.getRegion();
        this.profileImageUrl = userUpdateDTO.getProfileImageUrl();
        return this;
    }
}