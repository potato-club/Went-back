package com.example.demo.service;

import com.example.demo.error.InvalidTokenException;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.model.Member;
import com.example.demo.model.MemberStatus;
import com.example.demo.model.Role;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.MemberCreationRequest;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원 가입
    public Member createMember(MemberCreationRequest memberCreationRequest) {
        int passwordlength = memberCreationRequest.getPassword().length();

        if (userRepository.existsByUsername(memberCreationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (passwordlength < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters long");
        }

        // 비밀번호 암호화하여 저장
        String encodedPassword = passwordEncoder.encode(memberCreationRequest.getPassword());

        Member member = Member.builder()
                .username(memberCreationRequest.getUsername())
                .password(encodedPassword)
                .firstName(memberCreationRequest.getFirstName())
                .lastName(memberCreationRequest.getLastName())
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build();

        return userRepository.save(member);
    }


    // 로그인
    public JwtToken login(LoginRequest loginRequest) {
        // 사용자 정보 검증
        Member member = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("The username does not exist."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("The password is incorrect.");
        }

        // UsernamePasswordAuthenticationToken => 인증 후, SecurityContextHolder.getContext()에 등록될 Authentication 객체
        // 뭔 말임...
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getUsername(), loginRequest.getPassword(), member.getAuthorities());

        // JWT 생성 및 반환
        return jwtProvider.issueToken(authentication);
    }

    public JwtToken reissueToken(String refreshToken) {
        // resolve -> validate -> provide

        // RT 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid Refresh Token");
        }

        return jwtProvider.reissueToken(refreshToken);
    }


}
