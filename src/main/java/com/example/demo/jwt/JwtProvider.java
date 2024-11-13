/*
    JWT 토큰 생성
    로그인 요청 성공했을 때! (인증된 사용자 정보 바탕으로 토큰 생성)
 */

package com.example.demo.jwt;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.example.demo.jwt.JwtConstant.*;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;
    private final CustomUserDetailsService customUserDetailsService;
    
    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtProvider(@Value("${jwt.secret}") String secretKey, CustomUserDetailsService customUserDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 디코딩 된 keyBytes 배열을 Key 객체로 변환하여 key로 저장
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.customUserDetailsService = customUserDetailsService;
    }

    // AT 생성
    public String createAccessToken(String username, String authorities) {
        // now 변수에 현재 시간을 밀리초로 저장
        long now = (new Date()). getTime();
        Date accessTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(username) // 토큰 해석할 때 subject 값을 통해 어떤 사용자의 토큰인지 식별
                .claim("authorities", authorities)
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256) // key와 HS256 알고리즘을 사용하여 signature 생성
                .compact(); // 빌더가 설정한 값을 기반으로 JWT 문자열을 생성
    }

    // RT 생성
    public String createRefreshToken(String username, String authorities) {
        long now = (new Date()). getTime();
        Date refreshTokenExpiration = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(refreshTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 발급
    public JwtToken issueToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        String accessToken = createAccessToken(username, authorities);
        String refreshToken = createRefreshToken(username, authorities);

        log.info("Generated Access Token: Bearer " + accessToken);
        log.info("Generated Refresh Token: " + refreshToken);

        return JwtToken.builder() // JwtToken 객체를 빌더 패턴으로 생성
                .grantType(GRANT_TYPE) // 토큰의 인증 유형 설정
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 재발급
    public JwtToken reissueToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String username = claims.getSubject();
        String authorities = (String) claims.get("authoirities");

        String newAccessToken = createAccessToken(username, authorities);
        String newRefreshToken = createRefreshToken(username, authorities);

        log.info("Reissued Access Token: Bearer " + newAccessToken);
        log.info("Reissued Refresh Token: " + newRefreshToken);

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    // 토큰 정보 검증
    public boolean validateToken(String refreshtoken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshtoken);
            return true; 
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e); // 서명 오류 or 형식 오류
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e); // 만료
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e); // 잘못된 형식
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e); // 클레임 비어 있음
        }

        return false;
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // JWT 토큰의 서명 검증하기 위해 비밀 키 설정
                .build()
                .parseClaimsJws(token) // JWT 파싱 =>  JWS(JSON Web Signature) 객체로 변환
                .getBody(); // 파싱된 JWT에서 클레임 추출

        // 클레임에서 subject 가져오기 (=> 인증에 사용)
        String username = claims.getSubject();

        // CustomUserDetailsService로 해당 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        // 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }


}
