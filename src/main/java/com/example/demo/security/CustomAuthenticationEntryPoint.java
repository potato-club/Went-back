package com.example.demo.security;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // JwtAuthenticationFilter에서 request에 담아준 에러 코드를 꺼냅니다.
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        // 필터에서 넘어온 에러 코드가 없다면, 일반적인 "인증 실패" 코드를 사용합니다.
        // (토큰이 아예 없는 경우 등)
        if (errorCode == null) {
            errorCode = ErrorCode.UNAUTHORIZED;
        }

        String responseBody = objectMapper.writeValueAsString(ErrorResponseDto.toResponseEntity(errorCode).getBody());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}