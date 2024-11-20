package com.example.demo.service;

import com.example.demo.error.BadRequestException;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtToken;
import com.example.demo.model.Member;
import com.example.demo.model.MemberStatus;
import com.example.demo.model.Role;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.MemberCreationRequest;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Member createAdmin(@Valid MemberCreationRequest memberCreationRequest) {
        int passwordLength = memberCreationRequest.getPassword().length();

        if (passwordLength < 6) {
            throw new BadRequestException("6자 이상의 비밀번호만 가능합니다.", ErrorCode.PASSWORD_TOO_SHORT);
        }

        String encodedPassword = passwordEncoder.encode(memberCreationRequest.getPassword());

        Member member = Member.builder()
                .username(memberCreationRequest.getUsername())
                .password(encodedPassword)
                .firstName(memberCreationRequest.getFirstName())
                .lastName(memberCreationRequest.getLastName())
                .role(Role.ADMIN)
                .status(MemberStatus.ACTIVE)
                .build();

        return userRepository.save(member);
    }

    // 로그인
    public JwtToken adminLogin(LoginRequest loginRequest) {
        Member member = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.", ErrorCode.PASSWORD_INCORRECT);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getUsername(), loginRequest.getPassword(), member.getAuthorities());

        return jwtProvider.issueToken(authentication);
    }
}