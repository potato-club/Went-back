package com.example.demo.jwt;

import com.example.demo.entity.UserEntity;
import com.example.demo.error.*;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.jwt.JwtConstant.*;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;
    private final UserRepository userRepository;

    public JwtProvider(@Value("${jwt.secretKey}") String secretKey, UserRepository userRepository) {
        secretKey = secretKey.trim();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        if (keyBytes.length != 32) { // HMAC SHA256은 32바이트 필요
            throw new IllegalArgumentException("Invalid key length: " + keyBytes.length);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
    }

    public String createAccessToken(String username, String authorities) {
        try {
            long now = (new Date()).getTime();
            Date accessTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

            return Jwts.builder()
                    .setSubject(username)
                    .claim("authorities", authorities) // user
                    .setExpiration(accessTokenExpiration)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new TokenCreationException("Access Token이 생성되지 않았습니다.", ErrorCode.ACCESS_TOKEN_NOT_CREATED);
        }
    }

    public String createRefreshToken(String username) {
        try {
            long now = (new Date()).getTime();
            Date refreshTokenExpiration = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

            return Jwts.builder()
                    .setSubject(username)
                    .setExpiration(refreshTokenExpiration)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new TokenCreationException("Refresh Token이 생성되지 않았습니다.", ErrorCode.REFRESH_TOKEN_NOT_CREATED);
        }
    }

    public JwtToken issueToken(UserEntity user) {
        String username = user.getEmail();
        String authorities = "user"; // 일단 권한 정보 X

        String accessToken = createAccessToken(username, authorities);
        String refreshToken = createRefreshToken(username);

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public JwtToken reissueToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = getUsernameFromToken(refreshToken);

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 사용자입니다.", ErrorCode.USER_NOT_FOUND));

        String newAccessToken = createAccessToken(username, "user"); // role 없음
        String newRefreshToken = createRefreshToken(username);

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh-Token", refreshToken);
    }

    public boolean validateToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (IllegalArgumentException | MalformedJwtException e) {
            log.info("형식에 맞지 않는 토큰입니다.", e);
            throw new BadRequestException("잘못된 토큰 형식입니다.", ErrorCode.UNSUPPORTED_TOKEN); // 400
        } catch (SecurityException e) {
            log.info("유효하지 않는 JWT 서명입니다.", e);
            throw new UnAuthorizedException("잘못된 JWT 시그니처입니다.", ErrorCode.INVALID_SIGNATURE); // 401
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.", e);
            throw new UnAuthorizedException("인증이 만료된 토큰입니다.", ErrorCode.EXPIRED_TOKEN); // 401
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 형식입니다.", e);
            throw new UnAuthorizedException("지원되지 않는 토큰입니다.", ErrorCode.UNSUPPORTED_TOKEN); // 401
        }

    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        String authority = (String) claims.get("authorities"); // user

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

        UserEntity user = new UserEntity();
        user.setEmail(username); // 이메일만 담아둠

        return new UsernamePasswordAuthenticationToken(user, token, authorities);

    }

    public String resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh-Token");

        if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }

        if (StringUtils.hasText(refreshToken)) {
            return refreshToken;
        }

        return null;
    }

    // 이메일 반환
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

}
