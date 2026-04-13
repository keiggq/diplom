package com.example.demo.dto.response;

import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDate completedDate;
    
    private String assigneeName;
    private Long assigneeId;
    private String creatorName;
    private Long creatorId;
    
    private Long documentId;
    private String documentTitle;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String adminStatus;

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }
}
