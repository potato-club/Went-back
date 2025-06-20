package com.example.demo.social.kakao;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.ExternalAuthException;
import com.example.demo.social.kakao.dto.KakaoUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class KakaoIdTokenVerifierService {
    private static final String JWK_URL = "https://kauth.kakao.com/.well-known/jwks.json";

    @Value("${oauth.kakao.base-url}")
    private String kakaoBaseUrl;

    public KakaoUserInfo verify(String idToken) {
        try {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(JWK_URL)
                    .jwsAlgorithm(SignatureAlgorithm.RS256)
                    .build();

            Jwt jwt = jwtDecoder.decode(idToken);

            // 발급자(issuer) 검증
            URL issuer = jwt.getIssuer();
            URL expectedIssuer = new URL(kakaoBaseUrl);

            if (issuer == null || !expectedIssuer.equals(issuer)) {
                throw new IllegalArgumentException("ID Token의 발급자가 유효하지 않습니다.");
            }

            // 사용자(subject) 검증
            String subject = jwt.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("ID Token에 subject(사용자 식별자)가 존재하지 않습니다.");
            }

            return new KakaoUserInfo(
                    subject,
                    jwt.getClaimAsString("email")
            );

        } catch (JwtException | IllegalArgumentException | NullPointerException | MalformedURLException e) {
            throw new ExternalAuthException("카카오 ID Token 검증 실패", ErrorCode.EXTERNAL_AUTH_PROVIDER_ERROR);
        }
    }
}
