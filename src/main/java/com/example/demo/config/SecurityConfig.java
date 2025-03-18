//package com.example.demo.config;
//
//import com.example.demo.jwt.JwtAuthenticationFilter;
//import com.example.demo.jwt.JwtProvider;
//import com.example.demo.jwt.LoginFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@EnableWebSecurity
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//    private final AuthenticationConfiguration authenticationConfiguration;
//    private final JwtProvider jwtProvider;
//
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return webSecurity -> webSecurity.ignoring()
//                .requestMatchers("/error");
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf((auth) -> auth.disable());
//        http.formLogin((auth) -> auth.disable());
//        http.httpBasic((auth) -> auth.disable());
//        http.authorizeHttpRequests((auth) -> auth
//                .requestMatchers("/api/library/login", "/", "/api/library/signup", "/api/library/admin/signup", "/api/library/admin/login").permitAll()
//                .requestMatchers("/api/library/book", "/api/library/book/{bookId}", "/api/library/author").permitAll()
//                .requestMatchers("/api/library/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated());
//
//        http.addFilterAt(new LoginFilter(authenticationManager(), jwtProvider), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterAfter(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
//        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//}
