package com.example.demo.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtToken {
    private String grantType = "Bearer";
    private String accessToken;
    private String refreshToken;
}