package com.example.demo.controller;

import com.example.demo.jwt.JwtToken;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.MemberCreationRequest;
import com.example.demo.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/library")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/admin/signup")
    public ResponseEntity<String> adminSignup (@RequestBody @Valid MemberCreationRequest memberCreationRequest) {
        adminService.createAdmin(memberCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Hello, Administrator! XD");
    }

    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin (@RequestBody @Valid LoginRequest loginRequest) {
        JwtToken token = adminService.adminLogin(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization","Bearer " + token.getAccessToken())
                .header("RefreshToken", token.getRefreshToken())
                .body("Administrator Login Success♪♬");
    }

}
