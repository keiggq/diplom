package com.example.demo.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class DocumentCreateDto {
    
    private String title;
    private String description;
    private LocalDate documentDate;
    private LocalDate expiryDate;
    private Long authorId;
    private Long documentTypeId;
    private Long departmentId;
    private String keywords;
    private String version;
    private MultipartFile file;
}
