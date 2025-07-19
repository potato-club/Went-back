package com.example.demo.service;

import com.example.demo.dto.response.MyProfileResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.request.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    UserResponseDTO updateProfile(UserEntity currentUser, UserUpdateDTO userUpdateDTO);
    MyProfileResponseDTO getMyProfile(Long userId);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    void deleteUser(HttpServletRequest request);
}
