package com.example.demo.entity;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String socialKey;

    private String nickname;
    private String email;
    private LocalDate birthDate;
    private String region;

    @ElementCollection
    private List<Long> categoryIds;

    @Builder
    public UserEntity(String socialKey, String nickname, String email, LocalDate birthDate, String region, List<Long> categoryIds) {
        this.socialKey = socialKey;
        this.nickname = nickname;
        this.email = email;
        this.birthDate = birthDate;
        this.region = region;
        this.categoryIds = categoryIds;
    }

    public UserEntity(String email) {
        this.email = email;
    }

//    public UserResponseDTO toUserResponseDTO() {
//        UserResponseDTO userResponseDTO = new UserResponseDTO();
//        BeanUtils.copyProperties(this, userResponseDTO);
//        return userResponseDTO;
//    }

    public UserResponseDTO toUserResponseDTO() {
        return UserResponseDTO.builder()
                .socialKey(this.socialKey)
                .nickname(this.nickname)
                .email(this.email)
                .birthDate(this.birthDate)
                .region(this.region)
                .build();
    }

    public UserResponseDTO toUserLoginResponseDTO() {
        return UserResponseDTO.builder()
                .nickname(this.nickname)
                .email(this.email)
                .birthDate(this.birthDate)
                .region(this.region)
                .build();
    }


    public UserEntity updateByDto(UserUpdateDTO userUpdateDTO) {
        this.birthDate = LocalDate.parse(userUpdateDTO.getBirthDate());
        this.nickname = userUpdateDTO.getNickName();
        this.region = userUpdateDTO.getRegion();
        return this;
    }
}

