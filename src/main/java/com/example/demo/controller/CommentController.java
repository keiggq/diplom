package com.example.demo.controller;

import com.example.demo.dto.request.CommentCreateDto;
import com.example.demo.dto.request.CommentUpdateDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    /**
     * Добавление комментария к документу
     */
    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @RequestBody CommentCreateDto dto,
            @RequestAttribute("userId") Long authorId) {
        
        CommentDto createdComment = commentService.addComment(dto, authorId);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
    
    /**
     * Получение комментария по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }
    
    /**
     * Получение комментариев документа
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<CommentDto>> getCommentsByDocument(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "desc") String order) {
        
        return ResponseEntity.ok(commentService.getCommentsByDocumentOrdered(documentId, order));
    }
    
    /**
     * Получение комментариев пользователя
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CommentDto>> getCommentsByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(commentService.getCommentsByAuthor(authorId));
    }
    
    /**
     * Получение последних комментариев
     */
    @GetMapping("/recent")
    public ResponseEntity<List<CommentDto>> getRecentComments(
            @RequestParam(defaultValue = "10") int limit) {
        
        return ResponseEntity.ok(commentService.getRecentComments(limit));
    }
    
    /**
     * Обновление комментария
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateDto dto,
            @RequestAttribute("userId") Long userId) {
        
        return ResponseEntity.ok(commentService.updateComment(id, dto, userId));
    }
    
    /**
     * Удаление комментария
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute(value = "isAdmin", required = false) Boolean isAdmin) {
        
        commentService.deleteComment(id, userId, isAdmin != null && isAdmin);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Поиск комментариев
     */
    @GetMapping("/search")
    public ResponseEntity<List<CommentDto>> searchComments(@RequestParam String keyword) {
        return ResponseEntity.ok(commentService.searchComments(keyword));
    }
    
    /**
     * Комментарии с упоминанием пользователя
     */
    @GetMapping("/mentions/{username}")
    public ResponseEntity<List<CommentDto>> getCommentsMentioningUser(@PathVariable String username) {
        return ResponseEntity.ok(commentService.getCommentsMentioningUser(username));
    }
    
    /**
     * Количество комментариев к документу
     */
    @GetMapping("/count/document/{documentId}")
    public ResponseEntity<Long> getCommentCountByDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(commentService.getCommentCountByDocument(documentId));
    }
    
    /**
     * Количество комментариев пользователя
     */
    @GetMapping("/count/author/{authorId}")
    public ResponseEntity<Long> getCommentCountByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(commentService.getCommentCountByAuthor(authorId));
    }
}
