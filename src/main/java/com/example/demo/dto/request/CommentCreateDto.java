package com.example.demo.dto.request;

import lombok.Data;

@Data
public class CommentCreateDto {
    
    private String content;
    private Long documentId;
}
