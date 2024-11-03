package com.example.demo.service;

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
                () -> new IllegalArgumentException(username + " User Not Found.")
        );

        return new CustomUserDetails(user);
    }
}
