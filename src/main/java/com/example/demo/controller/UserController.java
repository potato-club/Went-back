package com.example.demo.controller;

import com.example.demo.dto.response.MyProfileResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 프로필 정보 입력/수정", description = "회원가입 이후, 사용자 프로필을 최초로 입력하거나 수정합니다.")
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UserUpdateDTO userUpdateDTO, @CurrentUser UserEntity currentUser) {
        UserResponseDTO userResponseDTO = userService.updateProfile(currentUser, userUpdateDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "마이페이지 조회", description = "로그인된 사용자의 마이페이지 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<MyProfileResponseDTO> getMyProfile(@CurrentUser CustomUserDetails currentUser) {
        MyProfileResponseDTO myProfileResponseDTO = userService.getMyProfile(currentUser.getUserId());
        return ResponseEntity.ok(myProfileResponseDTO);
    }

    @Operation(summary = "전체 사용자 조회", description = "등록된 모든 사용자를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "단일 사용자 조회", description = "ID로 특정 사용자를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

//    @Operation(summary = "단일 사용자 조회", description = "email+socialkey로 특정 사용자를 조회합니다.")
//    @PostMapping("/find")
//    public ResponseEntity<UserResponseDTO> getUser(@RequestBody UserUniqueDTO userUniqueDTO) {
//        return ResponseEntity.ok(userService.getUserBySocialKeyAndEmail(userUniqueDTO));
//    }

    @Operation(summary = "사용자 삭제", description = "로그인된 사용자를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        userService.deleteUser(request);
        return ResponseEntity.noContent().build();
    }
}