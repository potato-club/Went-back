package com.example.demo.service;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Member;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member user = userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User Not Found", ErrorCode.USER_NOT_FOUND)
        );

        return new CustomUserDetails(user);
    }
}
