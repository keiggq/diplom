package com.example.demo.entity;

import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Task extends BaseEntity {
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "completed_date")
    private LocalDate completedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    @JsonIgnore
    private User assignee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private User creator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    @JsonIgnore
    private Document document;
    
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = TaskStatus.NEW;
        }
        if (priority == null) {
            priority = TaskPriority.MEDIUM;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        if (status == TaskStatus.COMPLETED && completedDate == null) {
            completedDate = LocalDate.now();
        }
    }
}