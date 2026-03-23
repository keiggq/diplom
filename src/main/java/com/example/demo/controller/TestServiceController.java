package com.example.demo.controller;

import com.example.demo.entity.Department;
import com.example.demo.entity.User;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/services")
@RequiredArgsConstructor
public class TestServiceController {
    
    private final UserService userService;
    private final DepartmentService departmentService;
    
    @PostMapping("/register")
    public Map<String, Object> registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(required = false) Long departmentId) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.registerUser(username, email, password, fullName, departmentId);
            result.put("success", true);
            result.put("user", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    @PostMapping("/departments")
    public Department createDepartment(
            @RequestParam String name,
            @RequestParam String code,
            @RequestParam(required = false) String description) {
        
        return departmentService.createDepartment(name, code, description);
    }
    
    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
    
    @GetMapping("/departments/{id}/employees")
    public List<User> getDepartmentEmployees(@PathVariable Long id) {
        return departmentService.getDepartmentEmployees(id);
    }
}
