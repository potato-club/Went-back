package com.example.demo.dto;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class UserResponseDTO {
    private Long socialKey;
    private String nickname;
    private String email;
    private LocalDateTime birthDate;
    private String region;

    public Long getSocialKey() {
        return socialKey;
    }

    public void setSocialKey(Long socialKey) {
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
}

