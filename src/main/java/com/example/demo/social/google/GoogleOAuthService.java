package com.example.demo.social.google;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.social.google.dto.GoogleUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final GoogleIdTokenVerifierService googleIdTokenVerifierService;

    @Transactional
    public UserResponseDTO  loginWithGoogle(String idToken, HttpServletResponse response) {
        GoogleUserInfo googleUserInfo = googleIdTokenVerifierService.verify(idToken);

        UserEntity user = userRepository.findBySocialKey(googleUserInfo.getUserId())
                .orElse(null);

        boolean isNewUser = false;

        if (user == null) {
            // 신규 회원
            isNewUser = true;

            user = UserEntity.builder()
                    .socialKey(googleUserInfo.getUserId())
                    .email(googleUserInfo.getEmail())
                    .build();

            user = userRepository.save(user);
        }

        JwtToken jwtToken = jwtProvider.issueToken(user);
        jwtProvider.setHeaderAccessToken(response, jwtToken.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, jwtToken.getRefreshToken());

        if (isNewUser) {
            return user.toUserResponseDTO();
        } else {
            return user.toUserLoginResponseDTO();
        }
    }

}
