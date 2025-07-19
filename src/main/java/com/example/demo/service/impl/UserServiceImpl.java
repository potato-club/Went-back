package com.example.demo.service.impl;

import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.dto.response.MyProfileResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.request.UserUpdateDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.UserCategory;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.*;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final PostService postService;

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

    public MyProfileResponseDTO getMyProfile(Long userId) {
       UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

       MyProfileResponseDTO myProfile = userMapper.toMyProfileResponseDTO(user);

       List<PostPreviewResponseDTO> myPosts = postService.getMyPosts(userId);
       List<PostPreviewResponseDTO> likedPosts = postService.getMyLikedPosts(userId);

       return MyProfileResponseDTO.builder()
               .nickname(myProfile.getNickname())
               .region(myProfile.getRegion())
               .birthDate(myProfile.getBirthDate())
               .categories(myProfile.getCategories())
               .myPosts(myPosts)
               .likedPosts(likedPosts)
               .build();
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserEntity::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponseDTO(user);
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
}
