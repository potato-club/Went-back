package com.example.demo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String socialKey;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private String region;
    private List<Long> categoryIds;
    private String profileImageUrl;
}