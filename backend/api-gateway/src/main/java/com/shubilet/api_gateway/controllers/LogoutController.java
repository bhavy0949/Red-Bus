package com.shubilet.api_gateway.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie expired = new Cookie("jwt", "");
        expired.setHttpOnly(true);
        expired.setSecure(true);
        expired.setPath("/");
        expired.setMaxAge(0);   // instructs browser to delete the cookie immediately
        response.addCookie(expired);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
