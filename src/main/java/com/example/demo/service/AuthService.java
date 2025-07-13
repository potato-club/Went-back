package com.example.demo.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    public void reissueToken(HttpServletResponse response, String refreshToken);
}
