package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MyProfileResponseDTO {
    private String nickname;
    private String region;
    private LocalDate birthDate;
    private List<CategoryResponseDTO> categories;
    private List<PostPreviewResponseDTO> myPosts;
    private List<PostPreviewResponseDTO> likedPosts;
}
