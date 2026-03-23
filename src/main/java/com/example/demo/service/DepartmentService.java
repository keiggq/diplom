package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.jpa.DepartmentJpaRepository;
import com.example.demo.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentService {
    
    private final DepartmentJpaRepository departmentRepository;
    private final UserJpaRepository userRepository;
    
    /**
     * Создание отдела
     */
    public Department createDepartment(String name, String code, String description) {
        log.info("Создание отдела: {}", name);
        
        if (departmentRepository.existsByName(name)) {
            throw new RuntimeException("Отдел с таким названием уже существует");
        }
        
        Department department = new Department();
        department.setName(name);
        department.setCode(code);
        department.setDescription(description);
        
        Department savedDepartment = departmentRepository.save(department);
        log.info("Отдел создан с id: {}", savedDepartment.getId());
        
        return savedDepartment;
    }
    
    /**
     * Получение отдела по ID
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отдел не найден с id: " + id));
    }
    
    /**
     * Получение всех отделов
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    /**
     * Обновление отдела
     */
    public Department updateDepartment(Long id, String name, String code, String description) {
        Department department = getDepartmentById(id);
        
        department.setName(name);
        department.setCode(code);
        department.setDescription(description);
        
        return departmentRepository.save(department);
    }
    
    /**
     * Назначение руководителя отдела
     */
    public Department setHeadOfDepartment(Long departmentId, Long userId) {
        Department department = getDepartmentById(departmentId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));
        
        department.setHeadOfDepartment(user);
        return departmentRepository.save(department);
    }
    
    /**
     * Получение сотрудников отдела
     */
    public List<User> getDepartmentEmployees(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId);
    }
    
    /**
     * Количество сотрудников в отделе
     */
    public long getEmployeeCount(Long departmentId) {
        return userRepository.countByDepartmentId(departmentId);
    }
    
    /**
     * Удаление отдела
     */
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        
        // Проверяем, есть ли сотрудники
        if (getEmployeeCount(id) > 0) {
            throw new RuntimeException("Нельзя удалить отдел, в котором есть сотрудники");
        }
        
        departmentRepository.delete(department);
        log.info("Отдел удален с id: {}", id);
    }
}
