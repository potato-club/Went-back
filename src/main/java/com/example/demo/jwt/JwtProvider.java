package com.example.demo.jwt;

import com.example.demo.error.BadRequestException;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.TokenCreationException;
import com.example.demo.error.UnAuthorizedException;
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
    
    public JwtProvider(@Value("${jwt.secret}") String secretKey, CustomUserDetailsService customUserDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.customUserDetailsService = customUserDetailsService;
    }

    public String createAccessToken(String username, String authorities) {
        try {
            long now = (new Date()).getTime();
            Date accessTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

            return Jwts.builder()
                    .setSubject(username)
                    .claim("authorities", authorities)
                    .setExpiration(accessTokenExpiration)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new TokenCreationException("Access Token이 생성되지 않았습니다.", ErrorCode.ACCESS_TOKEN_NOT_CREATED);
        }
    }

    public String createRefreshToken(String username, String authorities) {
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

    public JwtToken issueToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        String accessToken = createAccessToken(username, authorities);
        String refreshToken = createRefreshToken(username, authorities);

        log.info("Generated Access Token: Bearer " + accessToken);
        log.info("Generated Refresh Token: " + refreshToken);

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

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

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

}
