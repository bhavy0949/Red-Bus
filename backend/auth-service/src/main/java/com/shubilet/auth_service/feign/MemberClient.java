package com.shubilet.auth_service.feign;

import com.shubilet.auth_service.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("/internal/users/load/{email}")
    UserDetailsDTO loadUserByEmail(@PathVariable("email") String email);
}
