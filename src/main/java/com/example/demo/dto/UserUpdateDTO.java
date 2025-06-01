package com.example.demo.dto;

import com.example.demo.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;


@Data
@Getter
public class UserUpdateDTO {
    @NotBlank(message = "nickname is required.")
    private String nickName;

    //optional value
//    @NotBlank(message = "Last name is required.")
    //yyyy-MM-dd
    private String birthDate;

    @NotBlank(message = "region is required.")// ??? is optional??
    private String region;
}