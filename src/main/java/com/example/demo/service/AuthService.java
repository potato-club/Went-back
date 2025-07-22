package com.example.demo.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

public interface AuthService {
    void saveRefreshToken(String username, String refreshToken);
    void reissueToken(HttpServletResponse response, String refreshToken);
}
