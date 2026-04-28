package com.example.demo.dto.response;

import com.example.demo.entity.DocumentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentDto {
    
    private Long id;
    private String title;
    private String registrationNumber;
    private String description;
    private LocalDate documentDate;
    private LocalDate creationDate;
    private LocalDate expiryDate;
    private DocumentStatus status;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String authorName;
    private Long authorId;
    private String documentTypeName;
    private String departmentName;
    private String keywords;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
