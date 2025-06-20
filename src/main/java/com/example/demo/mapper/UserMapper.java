package com.example.demo.mapper;

import com.example.demo.dto.response.CategoryResponseDTO;
import com.example.demo.dto.response.MyProfileResponseDTO;
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
}
