package com.example.demo.jwt;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.InvalidTokenException;
import com.example.demo.error.UnAuthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs/") || requestURI.equals("/api/oauth/google") || requestURI.equals("/api/oauth/kakao")) {
            chain.doFilter(request, response);
            return;
        }


        String accessToken = jwtProvider.resolveAccessToken(httpRequest);

        try {
            if (accessToken != null && jwtProvider.validateToken(accessToken)) {
                String isLogout = redisTemplate.opsForValue().get(accessToken);
                if ("logout".equals(isLogout)) {
                    throw new UnAuthorizedException("로그아웃된 토큰입니다.", ErrorCode.INVALID_ACCESS_TOKEN);
                }
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (InvalidTokenException | UnAuthorizedException e) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.getWriter().write("{\"message\": \"ErrorCode_401, 토큰이 없거나 유효하지 않습니다.\"}");
        }

    }
}