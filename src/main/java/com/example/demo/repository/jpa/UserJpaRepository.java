package com.example.demo.repository.jpa;

import com.example.demo.entity.User;
import com.example.demo.entity.Department;
import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    
    // Поиск по логину
    Optional<User> findByUsername(String username);
    
    // Поиск по email
    Optional<User> findByEmail(String email);
    
    // Проверка существования логина
    Boolean existsByUsername(String username);
    
    // Проверка существования email
    Boolean existsByEmail(String email);
    
    // Поиск по отделу
    List<User> findByDepartment(Department department);
    
    // Поиск по отделу (по ID)
    List<User> findByDepartmentId(Long departmentId);
    
    // Поиск по роли
    List<User> findByRole(Role role);
    
    // Поиск по части имени (без учета регистра)
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    
    // Поиск по отделу и роли
    List<User> findByDepartmentIdAndRole(Long departmentId, Role role);
    
    // Количество пользователей в отделе
    long countByDepartmentId(Long departmentId);
    
    // Поиск всех руководителей (MANAGER и ADMIN)
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' OR u.role = 'ROLE_ADMIN'")
    List<User> findAllManagers();
    
    // Поиск пользователей с активными задачами (через связанные задачи)
    @Query("SELECT DISTINCT u FROM User u JOIN Task t ON u.id = t.assignee.id WHERE t.status = 'IN_PROGRESS'")
    List<User> findUsersWithActiveTasks();
}