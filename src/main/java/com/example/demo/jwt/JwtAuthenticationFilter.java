package com.example.demo.jwt;

import com.example.demo.error.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs/") || requestURI.startsWith("/api/oauth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtProvider.resolveAccessToken(request);

        if (accessToken != null) {
            try {
                jwtProvider.validateToken(accessToken);

                String isLogout = redisTemplate.opsForValue().get(accessToken);
                if ("logout".equals(isLogout)) {
                    // 로그아웃된 토큰일 경우, request에 예외 정보를 저장
                    request.setAttribute("exception", ErrorCode.INVALID_ACCESS_TOKEN);
                } else {
                    // 토큰이 유효하면 인증 정보를 저장
                    Authentication authentication = jwtProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (SecurityException | MalformedJwtException e) {
                request.setAttribute("exception", ErrorCode.INVALID_SIGNATURE);
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN);
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN);
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", ErrorCode.INVALID_ACCESS_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }
}