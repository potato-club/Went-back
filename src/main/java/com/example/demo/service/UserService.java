package com.example.demo.service;

import com.example.demo.dto.UserCreationDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponseDTO createUser(UserCreationDTO userDTO, HttpServletResponse response);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUser(Long id);
    UserResponseDTO findUser(UserUniqueDTO userUniqueDTO);
    UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO, HttpServletRequest request);
    void deleteUser(Long id);
    void reissueToken(jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response);
}
