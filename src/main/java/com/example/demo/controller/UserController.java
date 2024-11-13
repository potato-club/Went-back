package com.example.demo.controller;

import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.MemberCreationRequest;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// 특정 URL로 요청을 보냈을 때, Controller의 어떤 메서드가 처리할지 매핑, value => 요청받을 URL 설정
@RequestMapping(value = "/api/library")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> createMember (@RequestBody @Valid MemberCreationRequest memberCreationRequest) {
        userService.createMember(memberCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Signup Success! XD");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> loginMember (@RequestBody @Valid LoginRequest loginRequest) {
        JwtToken token = userService.login(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization","Bearer " + token.getAccessToken())
                .header("RefreshToken", token.getRefreshToken())
                .body("Login Success♪♬");
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<String> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        JwtToken newToken = userService.reissueToken(refreshToken);

        return ResponseEntity.ok()
                .header("Authorization","Bearer " + newToken.getAccessToken())
                .header("RefreshToken", newToken.getRefreshToken())
                .body("Token Reissued Successfully!");
    }
}
