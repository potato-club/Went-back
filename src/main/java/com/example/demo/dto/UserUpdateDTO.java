package com.example.demo.dto;

import com.example.demo.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Data
@Getter
public class UserUpdateDTO {
    @NotBlank(message = "socialKey ( social login key ) is required value.")
    private String socialKey;
    @NotBlank(message = "email is required value.")
    private String email;

    @NotBlank(message = "nickname is required.")
    private String nickName;

    //optional value
//    @NotBlank(message = "Last name is required.")
    //yyyy-MM-dd
    private String birthDate;

    @NotBlank(message = "region is required.")// ??? is optional??
    private String region;

    @NotBlank(message = "Password is required.")
    private List<Long> categories = new ArrayList<>();
}