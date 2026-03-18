package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Department extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, length = 20)
    private String code;
    
    @Column(length = 500)
    private String description;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id")
    private User headOfDepartment;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> employees = new ArrayList<>();
    
    @OneToMany(mappedBy = "department")
    private List<Document> documents = new ArrayList<>();
    
    // Вспомогательные методы для управления связями
    public void addEmployee(User user) {
        employees.add(user);
        user.setDepartment(this);
    }
    
    public void removeEmployee(User user) {
        employees.remove(user);
        user.setDepartment(null);
    }
    
    public void setHeadOfDepartment(User user) {
        this.headOfDepartment = user;
        if (user != null && !user.getDepartment().equals(this)) {
            user.setDepartment(this);
        }
    }
}
