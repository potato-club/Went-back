package com.example.demo.jwt;

import com.example.demo.entity.UserEntity;
import com.example.demo.error.*;
import com.example.demo.redis.RefreshToken;
import com.example.demo.redis.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;

import static com.example.demo.jwt.JwtConstant.*;

@Slf4j
@Component
public class JwtProvider {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final String secretKey;
    private Key key;

    public JwtProvider(
            @Value("${jwt.secretKey}") String secretKey,
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.secretKey = secretKey;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey.trim());
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("JWT secret key must be 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken issueToken(UserEntity user) {
        String email = user.getEmail();
        String accessToken = createAccessToken(email, "user");
        String refreshToken = createRefreshToken(email);

        refreshTokenRepository.save(RefreshToken.builder()
                .email(email)
                .token(refreshToken)
                .build());

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtToken reissueToken(String refreshToken) {
        validateToken(refreshToken);
        String email = getUsernameFromToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new UnAuthorizedException("저장된 리프레시 토큰이 없습니다.", ErrorCode.INVALID_REFRESH_TOKEN));

        if (!storedToken.getToken().equals(refreshToken)) {
            throw new UnAuthorizedException("리프레시 토큰이 일치하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 사용자입니다.", ErrorCode.USER_NOT_FOUND));

        String newAccessToken = createAccessToken(email, "user");
        String newRefreshToken = createRefreshToken(email);

        storedToken.setToken(newRefreshToken);
        refreshTokenRepository.save(storedToken);

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(String refreshToken) {
        validateToken(refreshToken);
        String email = getUsernameFromToken(refreshToken);
        refreshTokenRepository.deleteById(email);
        log.info("RefreshToken 로그아웃 및 삭제 완료: {}", email);
    }

    public String createAccessToken(String username, String authorities) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .claim("authorities", authorities)
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new TokenCreationException("Access Token 생성 실패", ErrorCode.ACCESS_TOKEN_NOT_CREATED);
        }
    }

    public String createRefreshToken(String username) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new TokenCreationException("Refresh Token 생성 실패", ErrorCode.REFRESH_TOKEN_NOT_CREATED);
        }
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh-Token", refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException | MalformedJwtException e) {
            throw new BadRequestException("잘못된 토큰 형식입니다.", ErrorCode.UNSUPPORTED_TOKEN);
        } catch (SecurityException e) {
            throw new UnAuthorizedException("유효하지 않은 JWT 서명입니다.", ErrorCode.INVALID_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException("만료된 토큰입니다.", ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new UnAuthorizedException("지원되지 않는 토큰입니다.", ErrorCode.UNSUPPORTED_TOKEN);
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String username = claims.getSubject();
        String authority = (String) claims.get("authorities");

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

        UserEntity user = new UserEntity();
        user.setEmail(username);
        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("Refresh-Token");
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
