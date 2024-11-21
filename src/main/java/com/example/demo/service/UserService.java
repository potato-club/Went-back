package com.example.demo.service;

import com.example.demo.error.*;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.model.Member;
import com.example.demo.model.MemberStatus;
import com.example.demo.model.Role;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.MemberCreationRequest;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        int passwordLength = memberCreationRequest.getPassword().length();

        if (userRepository.existsByUsername(memberCreationRequest.getUsername())) {
            throw new ConflictException("이미 존재하는 ID입니다.", ErrorCode.USER_ALREADY_EXISTS);
        }

        if (passwordLength < 6) {
            throw new BadRequestException("6자 이상의 비밀번호만 가능합니다.", ErrorCode.PASSWORD_TOO_SHORT);
        }

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
        Member member = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.", ErrorCode.PASSWORD_INCORRECT);
        }

        // UsernamePasswordAuthenticationToken => 인증 후, SecurityContextHolder.getContext()에 등록될 Authentication 객체
        // 뭔 말임...
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getUsername(), loginRequest.getPassword(), member.getAuthorities());

        return jwtProvider.issueToken(authentication);
    }

    // 토큰 재발급
    // 여기 검증 로직 수정 ??
    public JwtToken reissueToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException("Refresh Token이 유효하지 않습니다.", ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return jwtProvider.reissueToken(refreshToken);
    }

    // 회원 탈퇴
    public void deleteMember (String username) {
        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));
        userRepository.delete(member);
    }

}
