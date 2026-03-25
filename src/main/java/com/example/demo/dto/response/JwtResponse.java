package com.example.demo.dto.response;

import com.example.demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String fullName;
    
    public JwtResponse(String token, Long id, String username, String email, Role role, String fullName) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }
}
