package com.shubilet.member_service.controllers;

import com.shubilet.member_service.dataTransferObjects.internal.UserDetailsDTO;
import com.shubilet.member_service.models.User;
import com.shubilet.member_service.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserRepository userRepository;

    public InternalUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/load/{email}")
    public ResponseEntity<UserDetailsDTO> loadByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(UserDetailsDTO.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
