package com.example.demo.mapper;

import com.example.demo.dto.response.CategoryResponseDTO;
import com.example.demo.dto.response.MyProfileResponseDTO;
import com.example.demo.dto.response.UserHomeResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final CategoryMapper categoryMapper;

    public MyProfileResponseDTO toMyProfileResponseDTO(UserEntity user) {
        return MyProfileResponseDTO.builder()
                .nickname(user.getNickname())
                .region(user.getRegion())
                .birthDate(user.getBirthDate())
                .categories(
                        user.getUserCategories().stream()
                                .map(userCategory -> categoryMapper.toCategoryResponseDTO(userCategory.getCategory()))
                                .toList()
                )
                .build();
    }

    public UserResponseDTO toUserResponseDTO(UserEntity user) {
        return UserResponseDTO.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .region(user.getRegion())
                .categoryIds(user.getUserCategories().stream()
                        .map(uc -> uc.getCategory().getCategoryId())
                        .toList())
                .build();
    }

    public UserHomeResponseDTO toUserHomeResponseDTO(UserEntity user) {
        return UserHomeResponseDTO.builder()
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .postCount(user.getPosts().size())
                .commentCount(user.getComments().size())
                .likeCount(user.getLikes().size())
                .build();
    }
}
