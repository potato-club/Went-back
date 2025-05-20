package com.example.demo.social.google;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.ExternalAuthException;
import com.example.demo.error.InvalidTokenException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleIdTokenVerifierService {

    @Value("${app.google.client.id}")
    private String googleClientId;

    public GoogleUserInfo verify(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new InvalidTokenException("유효하지 않은 Google ID Token입니다.", ErrorCode.INVALID_SOCIAL_TOKEN);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            return new GoogleUserInfo(
                    payload.getSubject(), // sub == 고유 ID == socialKey
                    payload.getEmail(),
                    (String) payload.get("name"),
                    (String) payload.get("picture")
            );
        } catch (Exception e) {
            throw new ExternalAuthException("Google ID Token 검증 실패", ErrorCode.EXTERNAL_AUTH_PROVIDER_ERROR);
        }
    }
}
