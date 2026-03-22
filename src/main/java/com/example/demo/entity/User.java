package com.example.demo.entity;

import com.example.demo.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(length = 100)
    private String position;
    
    @Column(length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;
    
    // Документы, созданные пользователем
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();
    
    // Задачи, назначенные пользователю
    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Task> assignedTasks = new ArrayList<>();
    
    // Задачи, созданные пользователем
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Task> createdTasks = new ArrayList<>();
    
    // Комментарии пользователя
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();
    
    // Вспомогательные методы
    public void addDocument(Document document) {
        documents.add(document);
        document.setAuthor(this);
    }
    
    public void removeDocument(Document document) {
        documents.remove(document);
        document.setAuthor(null);
    }
    
    public void addAssignedTask(Task task) {
        assignedTasks.add(task);
        task.setAssignee(this);
    }
    
    public void addCreatedTask(Task task) {
        createdTasks.add(task);
        task.setCreator(this);
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setAuthor(this);
    }
}