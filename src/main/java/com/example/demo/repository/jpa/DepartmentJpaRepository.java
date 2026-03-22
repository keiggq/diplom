package com.example.demo.repository.jpa;

import com.example.demo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<Department, Long> {
    
    // Поиск по названию
    Optional<Department> findByName(String name);
    
    // Поиск по коду
    Optional<Department> findByCode(String code);
    
    // Проверка существования названия
    boolean existsByName(String name);
    
    // Поиск отдела по руководителю
    Optional<Department> findByHeadOfDepartmentId(Long userId);
    
    // Количество сотрудников в отделе (через связанную таблицу)
    @Query("SELECT COUNT(u) FROM User u WHERE u.department.id = :departmentId")
    long countEmployeesByDepartmentId(Long departmentId);
    
    // Поиск отделов без руководителя
    @Query("SELECT d FROM Department d WHERE d.headOfDepartment IS NULL")
    java.util.List<Department> findDepartmentsWithoutHead();
}
