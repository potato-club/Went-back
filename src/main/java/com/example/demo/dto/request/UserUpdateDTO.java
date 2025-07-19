package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
public class UserUpdateDTO {
    @NotBlank(message = "nickname is required.")
    private String nickname;

    //optional value
    //yyyy-MM-dd
    @NotBlank(message = "birthDate is required.")
    private String birthDate;

    @NotBlank(message = "region is required.")// ??? is optional??
    private String region;

    // 유저가 선호하는 카테고리
    private List<Long> categoryIds = new ArrayList<>();

    private String profileImageUrl;
}