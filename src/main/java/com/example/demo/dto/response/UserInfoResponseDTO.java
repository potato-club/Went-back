package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponseDTO {
    private String nickname;
    private String region;
    private String profileImageUrl;
    private LocalDate birthDate;
    private List<CategoryResponseDTO> categories;
}
