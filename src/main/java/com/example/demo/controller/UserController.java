package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.UserEntity;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final JwtProvider jwtProvider;

    @Autowired
    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }


    @Operation(summary = "로그인", description = "이메일 + 소셜키로 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody UserLoginDTO loginDTO, HttpServletResponse response) {
        JwtToken token = userService.login(loginDTO);

        // 응답 헤더에도 토큰 추가
        jwtProvider.setHeaderAccessToken(response, token.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, token.getRefreshToken());

        return ResponseEntity.ok(token);
    }

    @Operation(summary = "로그아웃", description = "Refresh-Token 기반으로 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);


        if (refreshToken != null) {
            jwtProvider.logout(refreshToken); // 실제 삭제 처리
        }

        return ResponseEntity.noContent().build(); // 204
    }


    @Operation(summary = "토큰 재발급", description = "Refresh-Token으로 Access/Refresh 토큰을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<JwtToken> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);


        JwtToken token = jwtProvider.reissueToken(refreshToken);

        jwtProvider.setHeaderAccessToken(response, token.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, token.getRefreshToken());

        return ResponseEntity.ok(token);
    }

    @Operation(summary = "내 정보 조회", description = "JWT 토큰 기반으로 자신의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyInfo(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal(); // JWT에서 만든 사용자 객체
        UserResponseDTO dto = userService.getMyInfo(user.getEmail());
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreationDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }

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

    @Operation(summary = "사용자 수정", description = "ID에 해당하는 사용자 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "사용자 삭제", description = "ID에 해당하는 사용자를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}