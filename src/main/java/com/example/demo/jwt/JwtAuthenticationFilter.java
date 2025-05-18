package com.example.demo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // Swagger, 회원가입/로그인 관련 URI는 필터 적용 제외
        if (requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs/") ||
                requestURI.equals("/api/users") ||
                requestURI.equals("/api/users/login") ||
                requestURI.equals("/api/users/reissue") ||
                requestURI.equals("/api/users/logout")) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtProvider.resolveAccessToken(httpRequest);


        try {
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("인증 성공: {}", authentication.getName());
            } else {
                log.warn("인증 실패: 유효하지 않은 토큰 또는 토큰 없음. URI = {}", requestURI);
                sendUnauthorizedResponse((HttpServletResponse) response);
                return;
            }
        } catch (Exception e) {
            log.error("토큰 인증 중 예외 발생: {}", e.getMessage(), e);
            sendUnauthorizedResponse((HttpServletResponse) response);
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"message\": \"ErrorCode_401, 토큰이 없거나 유효하지 않습니다.\"}");
    }
}
