package com.example.demo.service;

import com.example.demo.dto.UserCreationDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.InvalidTokenException;
import com.example.demo.error.NotFoundException;
import com.example.demo.error.UnAuthorizedException;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userDTO, HttpServletResponse response) {
        UserEntity userEntity = new UserEntity();
        userEntity.setSocialKey(userDTO.getSocialKey());
        userEntity.setEmail(userDTO.getEmail());
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

    // 추가 정보 입력
    public UserResponseDTO updateUser(UserUpdateDTO userDTO, HttpServletRequest request) {
        UserEntity user = findUserByAccessToken(request);
        user.updateByDto(userDTO);
        UserEntity result = userRepository.save(user);
        return result.toUserResponseDTO();
    }



    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserEntity findUserByAccessToken(HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        String username = jwtProvider.getUsernameFromToken(token);

        return userRepository.findByEmail(username).orElseThrow(() ->
                new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));
    }

    // 토큰 재발급
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveToken(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
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
