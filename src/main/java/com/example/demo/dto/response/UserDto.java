package com.example.demo.dto.response;

import com.example.demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String position;
    private String phone;
    private Role role;
    private String departmentName;
    private Long departmentId;
}

