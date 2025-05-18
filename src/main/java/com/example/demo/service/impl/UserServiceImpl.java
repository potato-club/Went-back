package com.example.demo.service.impl;

import com.example.demo.dto.UserCreationDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.*;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userDTO, HttpServletResponse response) {
        if (userRepository.existsByEmail(userDTO.getEmail()) || userRepository.existsBySocialKey(userDTO.getSocialKey())) {
            throw new ConflictException("이미 존재하는 사용자입니다.", ErrorCode.USER_ALREADY_EXISTS);
        }
        
        UserEntity userEntity = UserEntity.builder()
                .socialKey(userDTO.getSocialKey())
                .email(userDTO.getEmail())
                .build();
        
        UserEntity result = userRepository.save(userEntity);

        // 소셜 키 검증

        JwtToken jwtToken = jwtProvider.issueToken(result);
        jwtProvider.setHeaderAccessToken(response, jwtToken.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, jwtToken.getRefreshToken());

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

//    public UserResponseDTO getMyProfile(HttpServletRequest request) {
//        UserEntity user = findUserByAccessToken(request);
//        return user.toUserResponseDTO();
//    }

    public UserResponseDTO updateUser(UserUpdateDTO userDTO, HttpServletRequest request) {
        UserEntity user = findUserByAccessToken(request);
        user.updateByDto(userDTO);
        UserEntity result = userRepository.save(user);
        return result.toUserResponseDTO();
    }

    public void deleteUser(HttpServletRequest request) {
        UserEntity user = findUserByAccessToken(request);
        userRepository.delete(user);
    }

    private UserEntity findUserByAccessToken(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveAccessToken(request);

        if (accessToken == null || accessToken.isBlank()) {
            throw new InvalidTokenException("Access Token이 존재하지 않습니다.", ErrorCode.INVALID_ACCESS_TOKEN);
        }
        
        String username = jwtProvider.getUsernameFromToken(accessToken);

        return userRepository.findByEmail(username).orElseThrow(() ->
                new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));
    }

    // 토큰 재발급
    public void reissueToken(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh Token이 존재하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnAuthorizedException("Refresh Token이 유효하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        JwtToken jwtToken = jwtProvider.reissueToken(refreshToken);

        jwtProvider.setHeaderAccessToken(response, jwtToken.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, jwtToken.getRefreshToken());
    }
}
