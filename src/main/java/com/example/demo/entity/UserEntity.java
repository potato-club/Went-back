package com.example.demo.entity;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String socialKey;

    private String nickname;
    private String email;
    private LocalDateTime birthDate;
    private String region;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSocialKey() {
        return socialKey;
    }

    public void setSocialKey(String socialKey) {
        this.socialKey = socialKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public UserResponseDTO toUserResponseDTO() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        BeanUtils.copyProperties(this, userResponseDTO);
        return userResponseDTO;
    }

    public UserEntity updateUser(UserUpdateDTO userUpdateDTO) {
        BeanUtils.copyProperties(this, userUpdateDTO);
        return this;
    }
}

