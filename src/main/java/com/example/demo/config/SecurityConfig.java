package com.example.demo.config;

import com.example.demo.jwt.JwtGenerator;
import com.example.demo.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtGenerator jwtGenerator;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF disable
        http.csrf((auth) -> auth.disable());

        // Form Login disable
        http.formLogin((auth) -> auth.disable());

        // http basic disable
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                // 모든 사용자에게 접근 허용 => 로그인/회원가입
                .requestMatchers("/api/library/login", "/", "/api/library/join").permitAll()

                // ROLE_ADMIN 권한 있는 사용자만 접근 허용
                .requestMatchers("/admin").hasRole("ADMIN")

                // 나머지 모든 요청 인증된 사용자만 접근 허용
                .anyRequest().authenticated());

        // 필터 추가 => 사용자 정의 필터로 로그인 과정 처리 (LoginFilter를 UsernamePasswordAuthenticationFilter 위치에서 동작하도록 설정)
        http.addFilterAt(new LoginFilter(authenticationManager(), jwtGenerator), UsernamePasswordAuthenticationFilter.class);

        // session 설정
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
