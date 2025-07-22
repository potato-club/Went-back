package com.example.demo.service.impl;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.InvalidTokenException;
import com.example.demo.error.UnAuthorizedException;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import static com.example.demo.jwt.JwtConstant.REFRESH_TOKEN_EXPIRE_TIME;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
                "RT:" + username,
                refreshToken,
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
    }

    public void reissueToken(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh Token이 존재하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnAuthorizedException("Refresh Token이 유효하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + username);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new UnAuthorizedException("저장된 Refresh Token과 일치하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        JwtToken jwtToken = jwtProvider.reissueToken(refreshToken);

        saveRefreshToken(username, jwtToken.getRefreshToken());

        jwtProvider.setHeaderAccessToken(response, jwtToken.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, jwtToken.getRefreshToken());
    }

    public void logout(String accessToken) {
        if (!jwtProvider.validateToken(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token 입니다.", ErrorCode.INVALID_ACCESS_TOKEN);
        }

        long expiration = jwtProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        String username = jwtProvider.getUsernameFromToken(accessToken);
        redisTemplate.delete("RT:" + username);
    }

}
