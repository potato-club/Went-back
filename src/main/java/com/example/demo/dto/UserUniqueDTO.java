package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;


@Data
@Getter
public class UserUniqueDTO {
    @NotBlank(message = "socialKey ( social login key ) is required value.")
    private String socialKey;
    @NotBlank(message = "email is required value.")
    private String email;
}