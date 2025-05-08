package com.example.demo.entity;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String socialKey;

    private String nickname;
    private String email;
    private LocalDate birthDate;
    private String region;

    @ElementCollection
    private List<Long> categoryIds;

    @Builder
    public UserEntity(String socialKey, String email) {
        this.socialKey = socialKey;
        this.email = email;
    }

    public UserEntity(String email) {
        this.email = email;
    }

    public UserResponseDTO toUserResponseDTO() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        BeanUtils.copyProperties(this, userResponseDTO);
        return userResponseDTO;
    }

    public UserEntity updateByDto(UserUpdateDTO userUpdateDTO) {
        this.birthDate = LocalDate.parse(userUpdateDTO.getBirthDate());
        this.nickname = userUpdateDTO.getNickName();
//        this.email = userUpdateDTO.getEmail();
        this.region = userUpdateDTO.getRegion();
//        this.categoryIds = userUpdateDTO.getCategories() != null ? userUpdateDTO.getCategories() : new ArrayList<>();
        return this;
    }
}

