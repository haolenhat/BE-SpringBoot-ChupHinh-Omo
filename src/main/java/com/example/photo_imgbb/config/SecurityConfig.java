package com.example.photo_imgbb.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) //  Thêm cấu hình CORS vào SecurityFilterChain
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để có thể gửi API từ Unity hoặc frontend
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login","/api/photos/**").permitAll() // Cho phép truy cập không cần đăng nhập
                        .anyRequest().authenticated() // Các API khác phải đăng nhập
                )
                .formLogin(login -> login.disable()) // Tắt form đăng nhập mặc định
                .httpBasic(basic -> basic.disable()); // Tắt HTTP Basic Authentication

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://omo-galery.netlify.app/")); // Cho phép React truy cập
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Cho phép gửi cookies/token nếu cần

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}