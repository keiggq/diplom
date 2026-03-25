package com.example.demo.dto.request;

import com.example.demo.entity.TaskPriority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateDto {
    
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long assigneeId;      // ID исполнителя
    private Long documentId;       // ID связанного документа (опционально)
}
