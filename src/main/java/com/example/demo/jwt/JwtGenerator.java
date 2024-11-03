/*
    JWT 토큰 생성
    로그인 요청 성공했을 때! (인증된 사용자 정보 바탕으로 토큰 생성)
 */

package com.example.demo.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.example.demo.jwt.JwtConstant.*;


@Component
public class JwtGenerator {
    private final Key key;
    
    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtGenerator(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 디코딩 된 keyBytes 배열을 Key 객체로 변환하여 key로 저장
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Member 정보로 AT, RT 생성 메소드
    public JwtToken generateToken(Long userId) {
        // now 변수에 현재 시간을 밀리초로 저장
        long now = (new Date()). getTime();

        // AT 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // 현재 시간 + 만료 시간

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId)) // 토큰 해석할 때 subject 값을 통해 어떤 사용자의 토큰인지 식별
//                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256) // key와 HS256 알고리즘을 사용하여 signature 생성
                .compact(); // 빌더가 설정한 값을 기반으로 JWT 문자열을 생성

        // RT 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder() // JwtToken 객체를 빌더 패턴으로 생성
                .grantType(GRANT_TYPE) // 토큰의 인증 유형 설정
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
}
