package com.example.demo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);
        // validateToken() => 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우, 토큰에서 Authentication 객체 가지고 와서 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 다음 filterchain 실행
            chain.doFilter(request, response);
        } else {
            // 토큰이 유효하지 않거나 없는 경우
            HttpServletResponse httpResponse = (HttpServletResponse) response; // ServletResponse를 HttpServletResponse로 캐스팅
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Invalid or missing token.\"}");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // Request Header에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authorization 헤더에 Bearer {token} 형태로 JWT 포함

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstant.GRANT_TYPE)) {
            return bearerToken.substring(7); // "Bearer " 제거하고 토큰 반환
        }

        return null;
    }
}
