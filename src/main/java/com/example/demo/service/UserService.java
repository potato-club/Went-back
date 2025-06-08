package com.example.demo.service;

import com.example.demo.dto.response.MyProfileResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponseDTO updateProfile(UserEntity currentUser, UserUpdateDTO userUpdateDTO);
    MyProfileResponseDTO getMyProfile(UserEntity currentUser);
//    UserResponseDTO createProfile(UserEntity currentUser, UserUpdateDTO userUpdateDTO);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUser(Long id);
    UserResponseDTO findUser(UserUniqueDTO userUniqueDTO);
//    UserResponseDTO updateUser(UserEntity currentUser, UserUpdateDTO userUpdateDTO);
    void deleteUser(HttpServletRequest request);
    void reissueToken(HttpServletResponse response, String refreshToken);
}
