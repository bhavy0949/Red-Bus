package com.shubilet.member_service.dataTransferObjects.internal;

import com.shubilet.member_service.common.enums.Role;
import com.shubilet.member_service.models.User;

public class UserDetailsDTO {

    private Long userId;
    private String email;
    private String password;
    private Role role;

    public UserDetailsDTO() {}

    public static UserDetailsDTO from(User user) {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.userId = user.getId();
        dto.email = user.getEmail();
        dto.password = user.getPassword();
        dto.role = user.getRole();
        return dto;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
}
