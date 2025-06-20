package com.example.demo.service.impl;

import com.example.demo.dto.response.MyProfileResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.UserCategory;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.*;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final CategoryRepository categoryRepository;
    private final UserMapper userMapper;

    // 추가 정보 입력 및 프로필 수정
    public UserResponseDTO updateProfile(UserEntity currentUser, UserUpdateDTO userUpdateDTO) {
        currentUser.updateByDto(userUpdateDTO);
        currentUser.getUserCategories().clear();

        List<Category> categories = categoryRepository.findAllById(userUpdateDTO.getCategoryIds());

        categories.forEach(category -> {
            UserCategory uc = new UserCategory();
            uc.assignUser(currentUser);   // 현재 유저와 연결
            uc.assignCategory(category);  // 카테고리와 연결
            currentUser.getUserCategories().add(uc);
        });

        UserEntity savedUser = userRepository.save(currentUser);
        return savedUser.toUserResponseDTO();
    }

    // 내 프로필 조회
    public MyProfileResponseDTO getMyProfile(UserEntity currentUser) {
        return userMapper.toMyProfileResponseDTO(currentUser);
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



//    public UserResponseDTO updateUser(UserEntity currentUser, UserUpdateDTO useUpdateDTO) {
//        currentUser.updateByDto(useUpdateDTO);
//        UserEntity savedUser = userRepository.save(currentUser);
//        return savedUser.toUserResponseDTO();
//    }

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
