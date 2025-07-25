package com.example.demo.social.kakao;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.social.kakao.dto.KakaoTokenResponse;
import com.example.demo.social.kakao.dto.KakaoUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoIdTokenVerifierService kakaoIdTokenVerifierService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @Value( "${kakao.client-id}")
    private String clientId;

    @Value( "${kakao.client-secret:}")
    private String clientSecret;

    @Value( "${kakao.redirect-uri}")
    private String redirectUri;

    @Transactional
    public UserResponseDTO loginWithKakao(String code, HttpServletResponse response) {
        KakaoTokenResponse kakaoTokenResponse = kakaoTokenClient.getToken(code, clientId, redirectUri, clientSecret);
        KakaoUserInfo kakaoUserInfo = kakaoIdTokenVerifierService.verify(kakaoTokenResponse.getIdToken());

        UserEntity user = userRepository.findBySocialKey(kakaoUserInfo.getUserId())
                .orElse(null);

        boolean isNewUser = false;

        if (user == null) {
            // 신규 회원
            isNewUser = true;

            user = UserEntity.builder()
                    .socialKey(kakaoUserInfo.getUserId())
                    .email(kakaoUserInfo.getEmail())
                    .build();

            user = userRepository.save(user);
        }

        JwtToken jwtToken = jwtProvider.issueToken(user);
        jwtProvider.setHeaderAccessToken(response, jwtToken.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, jwtToken.getRefreshToken());

        authService.saveRefreshToken(user.getEmail(), jwtToken.getRefreshToken());

        if (isNewUser) {
            return user.toUserResponseDTO();
        } else {
            return user.toUserLoginResponseDTO();
        }
    }
}
