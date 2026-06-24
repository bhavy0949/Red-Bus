package com.shubilet.auth_service.controllers;

import com.shubilet.auth_service.dto.LoginRequest;
import com.shubilet.auth_service.dto.UserDetailsDTO;
import com.shubilet.auth_service.feign.MemberClient;
import com.shubilet.auth_service.service.JwtService;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberClient memberClient;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(MemberClient memberClient, JwtService jwtService) {
        this.memberClient = memberClient;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        UserDetailsDTO user;
        try {
            user = memberClient.loadUserByEmail(request.getEmail());
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        String token = jwtService.generateToken(user.getUserId(), user.getRole());

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) (86400));  // 24 h
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }
}
