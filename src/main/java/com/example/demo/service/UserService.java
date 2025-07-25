package com.example.demo.service;

import com.example.demo.dto.response.UserHomeResponseDTO;
import com.example.demo.dto.response.UserInfoResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.request.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    UserResponseDTO updateProfile(UserEntity currentUser, UserUpdateDTO userUpdateDTO);
    UserInfoResponseDTO getUserInfo(Long userId);
    UserHomeResponseDTO getUserHomeResponseDTO(Long userId);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    void deleteUser(HttpServletRequest request);
}
