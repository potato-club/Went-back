package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.ConflictException;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.UnAuthorizedException;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final JwtProvider jwtProvider;


    public JwtToken login(UserLoginDTO loginDTO) {
        List<UserEntity> users = userRepository.findBySocialKeyAndEmail(loginDTO.getSocialKey(), loginDTO.getEmail());

        if (users.size() != 1) {
            throw new UnAuthorizedException("로그인 정보가 올바르지 않습니다.", ErrorCode.USER_NOT_FOUND);
        }

        UserEntity user = users.get(0);
        return jwtProvider.issueToken(user);
    }

    public UserResponseDTO getMyInfo(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 사용자입니다.", ErrorCode.USER_NOT_FOUND));
        return user.toUserResponseDTO();
    }


    public UserResponseDTO createUser(UserCreationDTO userDTO) {
        List<UserEntity> exists = userRepository.findBySocialKeyAndEmail(
                userDTO.getSocialKey(), userDTO.getEmail()
        );

        if (!exists.isEmpty()) {
            throw new ConflictException("이미 등록된 사용자입니다.", ErrorCode.USER_ALREADY_EXISTS);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setSocialKey(userDTO.getSocialKey());
        userEntity.setEmail(userDTO.getEmail());
        UserEntity result = userRepository.save(userEntity);
        return result.toUserResponseDTO();
    }


    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserEntity::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        return userEntity != null ? userEntity.toUserResponseDTO() : null;
    }

    public UserResponseDTO findUser(UserUniqueDTO userUniqueDTO) {
        List<UserEntity> listResult = userRepository.findBySocialKeyAndEmail(userUniqueDTO.getSocialKey(), userUniqueDTO.getEmail());
        if (listResult.size() != 1) {
            return null;
        }
        UserEntity userEntity = listResult.get(0);
        return userEntity != null ? userEntity.toUserResponseDTO() : null;
    }

    public UserResponseDTO updateUser(UserUpdateDTO userDTO) {
        List<UserEntity> listResult = userRepository.findBySocialKeyAndEmail(userDTO.getSocialKey(), userDTO.getEmail());
        if (listResult.size() != 1) {
            return null;
        }
        UserEntity userEntity = listResult.get(0);
        userEntity.updateByDto(userDTO);
        UserEntity result = userRepository.save(userEntity);
        return result.toUserResponseDTO();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}