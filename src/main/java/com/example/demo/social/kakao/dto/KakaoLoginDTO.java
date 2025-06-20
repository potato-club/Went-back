package com.example.demo.social.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginDTO {
    private String code; // 카카오 로그인 시, 프론트가 보내 주는 인가 코드
}
