package com.example.demo.service;

import com.example.demo.dto.request.CommentCreateDto;
import com.example.demo.dto.request.CommentUpdateDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Document;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.jpa.CommentJpaRepository;
import com.example.demo.repository.jpa.DocumentJpaRepository;
import com.example.demo.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {
    
    private final CommentJpaRepository commentRepository;
    private final DocumentJpaRepository documentRepository;
    private final UserJpaRepository userRepository;
    
    // Регулярное выражение для поиска упоминаний @username
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");
    
    /**
     * Конвертация Comment в CommentDto
     */
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorName(comment.getAuthor().getFullName());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setDocumentId(comment.getDocument().getId());
        dto.setDocumentTitle(comment.getDocument().getTitle());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
    
    /**
     * Поиск упоминаний в комментарии
     */
    private List<String> findMentions(String content) {
        Matcher matcher = MENTION_PATTERN.matcher(content);
        return matcher.results()
                .map(match -> match.group(1))
                .collect(Collectors.toList());
    }
    
    /**
     * Добавление комментария к документу
     */
    public CommentDto addComment(CommentCreateDto dto, Long authorId) {
        log.info("Добавление комментария к документу {}", dto.getDocumentId());
        
        Document document = documentRepository.findById(dto.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + dto.getDocumentId()));
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + authorId));
        
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setDocument(document);
        comment.setAuthor(author);
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий добавлен с id: {}", savedComment.getId());
        
        // Поиск упоминаний (можно добавить логику уведомлений позже)
        List<String> mentions = findMentions(dto.getContent());
        if (!mentions.isEmpty()) {
            log.info("Упоминания в комментарии: {}", mentions);
            // TODO: Отправить уведомления упомянутым пользователям
        }
        
        return convertToDto(savedComment);
    }
    
    /**
     * Получение комментария по ID
     */
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий не найден с id: " + id));
        return convertToDto(comment);
    }
    
    /**
     * Получение всех комментариев документа
     */
    public List<CommentDto> getCommentsByDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + documentId));
        
        return commentRepository.findByDocument(document)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение комментариев документа с сортировкой по дате
     */
    public List<CommentDto> getCommentsByDocumentOrdered(Long documentId, String order) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + documentId));
        
        List<Comment> comments;
        if ("asc".equalsIgnoreCase(order)) {
            comments = commentRepository.findByDocument(document);
            // Сортировка по возрастанию
            comments.sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
        } else {
            comments = commentRepository.findByDocumentIdOrderByCreatedAtDesc(documentId);
        }
        
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение комментариев пользователя
     */
    public List<CommentDto> getCommentsByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + authorId));
        
        return commentRepository.findByAuthor(author)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение последних комментариев
     */
    public List<CommentDto> getRecentComments(int limit) {
        return commentRepository.findRecentComments(limit)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновление комментария
     */
    public CommentDto updateComment(Long id, CommentUpdateDto dto, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий не найден с id: " + id));
        
        // Проверка прав: только автор может редактировать
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Нет прав на редактирование этого комментария");
        }
        
        comment.setContent(dto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Комментарий {} обновлен", id);
        
        return convertToDto(updatedComment);
    }
    
    /**
     * Удаление комментария
     */
    public void deleteComment(Long id, Long userId, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий не найден с id: " + id));
        
        // Проверка прав: автор или администратор могут удалить
        if (!comment.getAuthor().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Нет прав на удаление этого комментария");
        }
        
        commentRepository.delete(comment);
        log.info("Комментарий с id {} удален", id);
    }
    
    /**
     * Поиск комментариев по содержимому
     */
    public List<CommentDto> searchComments(String keyword) {
        return commentRepository.findByContentContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение комментариев с упоминанием пользователя
     */
    public List<CommentDto> getCommentsMentioningUser(String username) {
        return commentRepository.findCommentsMentioningUser(username)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Количество комментариев к документу
     */
    public long getCommentCountByDocument(Long documentId) {
        return commentRepository.countByDocumentId(documentId);
    }
    
    /**
     * Количество комментариев пользователя
     */
    public long getCommentCountByAuthor(Long authorId) {
        return commentRepository.countByAuthorId(authorId);
    }
}
