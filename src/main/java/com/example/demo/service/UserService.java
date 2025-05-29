package com.example.demo.service;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUser(Long id);
    UserResponseDTO findUser(UserUniqueDTO userUniqueDTO);
    UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO, HttpServletRequest request);
    void deleteUser(HttpServletRequest request);
    void reissueToken(HttpServletResponse response, String refreshToken);
}
