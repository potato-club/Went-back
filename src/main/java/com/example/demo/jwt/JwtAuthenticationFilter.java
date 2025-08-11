package com.example.demo.jwt;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.InvalidTokenException;
import com.example.demo.error.UnAuthorizedException;
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
import org.springframework.web.filter.OncePerRequestFilter; // OncePerRequestFilter로 변경

import java.io.IOException;

@Component
@RequiredArgsConstructor
// GenericFilterBean 대신 OncePerRequestFilter를 상속하여 요청당 필터가 한 번만 실행되도록 보장합니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 경로는 필터를 통과시킵니다.
        if (requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs/") || requestURI.startsWith("/api/oauth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtProvider.resolveAccessToken(request);

        // try-catch로 응답을 직접 만드는 대신, 예외를 그대로 던져서
        // SecurityConfig에 등록된 핸들러가 처리하도록 위임합니다.
        if (accessToken != null) {
            try {
                jwtProvider.validateToken(accessToken); // 토큰 유효성 검증

                String isLogout = redisTemplate.opsForValue().get(accessToken);
                if ("logout".equals(isLogout)) {
                    // 로그아웃된 토큰일 경우, 명시적인 예외 발생
                    throw new UnAuthorizedException("로그아웃된 토큰입니다.", ErrorCode.INVALID_ACCESS_TOKEN);
                }

                // 토큰이 유효하면 인증 정보를 SecurityContext에 저장
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (SecurityException | MalformedJwtException e) {
                // 더 구체적인 예외를 던져 상세한 에러 코드를 전달합니다.
                throw new InvalidTokenException("손상된 토큰입니다.", ErrorCode.INVALID_SIGNATURE);
            } catch (ExpiredJwtException e) {
                throw new InvalidTokenException("만료된 토큰입니다.", ErrorCode.EXPIRED_TOKEN);
            } catch (UnsupportedJwtException e) {
                throw new InvalidTokenException("지원되지 않는 토큰입니다.", ErrorCode.UNSUPPORTED_TOKEN);
            } catch (IllegalArgumentException e) {
                throw new InvalidTokenException("토큰이 비어있거나 잘못되었습니다.", ErrorCode.INVALID_ACCESS_TOKEN);
            }
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }
}