package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserHomeResponseDTO {
    private String nickname;
    private String profileImageUrl;
    private int postCount;
    private int commentCount;
    private int likeCount;
}
