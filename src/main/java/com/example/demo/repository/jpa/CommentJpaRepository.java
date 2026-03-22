package com.example.demo.repository.jpa;

import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    
    // Комментарии к документу
    List<Comment> findByDocument(Document document);
    
    // Комментарии к документу (по ID) с сортировкой по дате
    List<Comment> findByDocumentIdOrderByCreatedAtDesc(Long documentId);
    
    // Комментарии автора
    List<Comment> findByAuthor(User author);
    
    // Комментарии автора (по ID)
    List<Comment> findByAuthorId(Long authorId);
    
    // Последние N комментариев
    @Query(value = "SELECT * FROM comments ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Comment> findRecentComments(@Param("limit") int limit);
    
    // Комментарии за период
    List<Comment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Количество комментариев к документу
    long countByDocumentId(Long documentId);
    
    // Количество комментариев пользователя
    long countByAuthorId(Long authorId);
    
    // Поиск комментариев по содержимому
    List<Comment> findByContentContainingIgnoreCase(String keyword);
    
    // Комментарии с упоминаниями (@username)
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:username%")
    List<Comment> findCommentsMentioningUser(@Param("username") String username);
}

