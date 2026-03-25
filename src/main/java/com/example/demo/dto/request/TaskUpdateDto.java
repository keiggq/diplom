package com.example.demo.dto.request;

import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskUpdateDto {
    
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long assigneeId;
}
