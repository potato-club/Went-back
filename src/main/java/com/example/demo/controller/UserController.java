package com.example.demo.controller;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

//    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
//    @PostMapping
//    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreationDTO userDTO, HttpServletResponse response) {
//        return ResponseEntity.ok(userService.createUser(userDTO, response));
//    }

    @Operation(summary = "전체 사용자 조회", description = "등록된 모든 사용자를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "단일 사용자 조회", description = "ID로 특정 사용자를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        UserResponseDTO userDTO = userService.getUser(id);
        return userDTO != null ? ResponseEntity.ok(userDTO) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "단일 사용자 조회", description = "email+socialkey로 특정 사용자를 조회합니다.")
    @PostMapping("/find")
    public ResponseEntity<UserResponseDTO> getUser(@RequestBody UserUniqueDTO userUniqueDTO) {
        UserResponseDTO userDTO = userService.findUser(userUniqueDTO);
        return userDTO != null ? ResponseEntity.ok(userDTO) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "사용자 추가 정보 입력", description = "사용자 정보를 수정합니다.")
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO, HttpServletRequest request) {
        UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO, request);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "사용자 삭제", description = "로그인된 사용자를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        userService.deleteUser(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "토큰 재발급", description = "AT, RT를 재발급합니다.", security = @SecurityRequirement(name = ""))
    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken(HttpServletResponse response, @RequestHeader("Refresh-Token") String refreshToken) {
        userService.reissueToken(response, refreshToken);
        return ResponseEntity.noContent().build();
    }
}