package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Department;
import com.example.demo.entity.Role;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.jpa.UserJpaRepository;
import com.example.demo.repository.jpa.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserJpaRepository userRepository;
    private final DepartmentJpaRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Регистрация нового пользователя
     */
    public User registerUser(String username, String email, String password, String fullName, Long departmentId) {
        log.info("Регистрация нового пользователя: {}", username);
        
        // Проверка уникальности
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Имя пользователя уже занято: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email уже используется: " + email);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(Role.ROLE_USER);
        
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Отдел не найден с id: " + departmentId));
            user.setDepartment(department);
        }
        
        User savedUser = userRepository.save(user);
        log.info("Пользователь зарегистрирован с id: {}", savedUser.getId());
        
        return savedUser;
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
     * Получение всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Получение пользователей по отделу
     */
    public List<User> getUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId);
    }
    
    /**
     * Обновление пользователя
     */
    public User updateUser(Long id, String fullName, String position, String phone) {
        User user = getUserById(id);
        
        user.setFullName(fullName);
        user.setPosition(position);
        user.setPhone(phone);
        
        return userRepository.save(user);
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
