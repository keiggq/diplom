package com.example.demo.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    
    private Long id;
    private String content;
    private String authorName;
    private Long authorId;
    private Long documentId;
    private String documentTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorPosition;
    public String getAuthorPosition() {
        return authorPosition;
    }   
    public void setAuthorPosition(String authorPosition) {
        this.authorPosition = authorPosition;
    }
}
