package com.example.demo.controller;

import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.dto.response.UserInfoResponseDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.request.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.PostService;
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
    private final PostService postService;

    @Operation(summary = "사용자 프로필 정보 입력/수정", description = "회원가입 이후, 사용자 프로필을 최초로 입력하거나 수정합니다.")
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UserUpdateDTO userUpdateDTO, @CurrentUser UserEntity currentUser) {
        UserResponseDTO userResponseDTO = userService.updateProfile(currentUser, userUpdateDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "내 정보 조회", description = "마이페이지의 사용자 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDTO> getUserInfo(@CurrentUser CustomUserDetails currentUser) {
        UserInfoResponseDTO userInfoResponseDTO = userService.getUserInfo(currentUser.getUserId());
        return ResponseEntity.ok(userInfoResponseDTO);
    }

    @Operation(summary = "내가 쓴 게시글 목록 조회", description = "마이페이지에서 내가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/me/posts")
    public ResponseEntity<List<PostPreviewResponseDTO>> getMyPosts(@CurrentUser CustomUserDetails currentUser) {
        List<PostPreviewResponseDTO> myPosts = postService.getMyPosts(currentUser.getUserId());
        return ResponseEntity.ok(myPosts);
    }

    @Operation(summary = "내가 좋아요 누른 게시글 목록 조회", description = "마이페이지에서 내가 좋아요 누른 게시글 목록을 조회합니다.")
    @GetMapping("/me/likes")
    public ResponseEntity<List<PostPreviewResponseDTO>> getMyLikedPosts(@CurrentUser CustomUserDetails currentUser) {
        List<PostPreviewResponseDTO> likedPosts = postService.getMyLikedPosts(currentUser.getUserId());
        return ResponseEntity.ok(likedPosts);
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