package com.example.demo.service.impl;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.InvalidTokenException;
import com.example.demo.error.UnAuthorizedException;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.redis.RefreshToken;
import com.example.demo.redis.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    // 토큰 재발급
    public void reissueToken(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh Token이 존재하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnAuthorizedException("Refresh Token이 유효하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        JwtToken jwtToken = jwtProvider.reissueToken(refreshToken);

        String email = jwtProvider.getUsernameFromToken(jwtToken.getRefreshToken());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(email)
                        .token(jwtToken.getRefreshToken())
                        .build()
        );

        jwtProvider.setHeaderAccessToken(response, jwtToken.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, jwtToken.getRefreshToken());
    }
}
