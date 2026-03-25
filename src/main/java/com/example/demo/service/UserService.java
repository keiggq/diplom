package com.example.demo.service;

import com.example.demo.dto.request.SignupRequest;
import com.example.demo.dto.response.UserDto;
import com.example.demo.entity.Department;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.jpa.DepartmentJpaRepository;
import com.example.demo.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserJpaRepository userRepository;
    private final DepartmentJpaRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Конвертация User в UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPosition(user.getPosition());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        
        if (user.getDepartment() != null) {
            dto.setDepartmentName(user.getDepartment().getName());
            dto.setDepartmentId(user.getDepartment().getId());
        }
        
        return dto;
    }
    
    /**
     * Регистрация нового пользователя (принимает SignupRequest)
     */
    public UserDto registerUser(SignupRequest request) {
        log.info("Регистрация нового пользователя: {}", request.getUsername());
        
        // Проверка уникальности
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Имя пользователя уже занято: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже используется: " + request.getEmail());
        }
        
        // Создаем пользователя
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPosition(request.getPosition());
        user.setPhone(request.getPhone());
        user.setRole(Role.ROLE_USER);
        
        // Привязка к отделу, если указан
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Отдел не найден с id: " + request.getDepartmentId()));
            user.setDepartment(department);
        }
        
        User savedUser = userRepository.save(user);
        log.info("Пользователь зарегистрирован с id: {}", savedUser.getId());
        
        return convertToDto(savedUser);
    }
    
    /**
     * Получение пользователя по ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + id));
    }
    
    /**
     * Получение пользователя по логину
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
    }
    
    /**
     * Получение всех пользователей (DTO)
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение пользователей по отделу
     */
    public List<UserDto> getUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновление пользователя
     */
    public UserDto updateUser(Long id, String fullName, String position, String phone) {
        User user = getUserById(id);
        
        user.setFullName(fullName);
        user.setPosition(position);
        user.setPhone(phone);
        
        return convertToDto(userRepository.save(user));
    }
    
    /**
     * Смена пароля
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = getUserById(id);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Старый пароль неверен");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Пароль изменен для пользователя: {}", user.getUsername());
    }
    
    /**
     * Удаление пользователя
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Пользователь удален с id: {}", id);
    }
    
    /**
     * Количество пользователей
     */
    public long getUserCount() {
        return userRepository.count();
    }
}