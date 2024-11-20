/*
   클라이언트가 토큰을 Authorization 헤어데 담아서 보냄
   이 필터에서 Authorization 헤더에서 토큰 검증!
 */
package com.example.demo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String requestURI = httpRequest.getRequestURI();

        // 회원가입, 로그인, 관리자 가입, 도서 조회 경로에서 토큰 검증 건너뛰도록 처리
        List<String> exemptedPaths = Arrays.asList(
                "/api/library/signup",
                "/api/library/login",
                "/api/library/admin/signup",
                "/api/library/admin/login",
                "/api/library/book",
                "/api/library/book/{bookId}",
                "/api/library/author"
        );

        if (exemptedPaths.contains(requestURI)) {
            chain.doFilter(request, response); // 토큰 검증 없이 다음 필터로 넘기기
            return;
        }

         String token = resolveToken((HttpServletRequest) request);

        // validateToken() => 토큰 유효성 검사
        if (token != null && jwtProvider.validateToken(token)) {
            // 토큰이 유효할 경우, 토큰에서 Authentication 객체 가지고 와서 SecurityContext에 저장
            Authentication authentication = jwtProvider.getAuthentication(token); // Authentication 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 인증 정보 저장
            chain.doFilter(request, response); // 다음 filterchain 실행
        }
    }

    // Request Header에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshToken");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거하고 토큰 반환
        }

        if (StringUtils.hasText(refreshToken)) {
            return refreshToken;
        }

        return null;
    }
}