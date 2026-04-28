package com.example.demo.repository.jpa;

import com.example.demo.entity.Document;
import com.example.demo.entity.User;
import com.example.demo.entity.Department;
import com.example.demo.entity.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentJpaRepository extends JpaRepository<Document, Long> {
    
    // ===== БАЗОВЫЕ ПОИСКИ =====
    
    // Поиск по автору
    List<Document> findByAuthor(User author);
    
    // Поиск по автору с пагинацией
    Page<Document> findByAuthor(User author, Pageable pageable);
    
    // Поиск по отделу
    List<Document> findByDepartment(Department department);
    
    // Поиск по статусу
    List<Document> findByStatus(DocumentStatus status);
    
    // Поиск по статусу с пагинацией
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    
    // Поиск по типу документа
    List<Document> findByDocumentTypeId(Long typeId);
    
    // Поиск по регистрационному номеру
    Optional<Document> findByRegistrationNumber(String registrationNumber);
    
    
    // ===== ПОИСК ПО ДАТАМ =====
    
    // Поиск по дате документа
    List<Document> findByDocumentDate(LocalDate date);
    
    // Поиск по диапазону дат
    List<Document> findByDocumentDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Поиск по дате создания
    List<Document> findByCreationDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Поиск просроченных документов
    @Query("SELECT d FROM Document d WHERE d.expiryDate < CURRENT_DATE AND d.status != 'ARCHIVED'")
    List<Document> findExpiredDocuments();
    
    // Документы, истекающие в ближайшие N дней
    @Query("SELECT d FROM Document d WHERE d.expiryDate BETWEEN CURRENT_DATE AND :date")
    List<Document> findDocumentsExpiringBefore(@Param("date") LocalDate date);
    
    
    // ===== ПОИСК С КЛЮЧЕВЫМИ СЛОВАМИ =====
    
    // Полнотекстовый поиск
    @Query("SELECT d FROM Document d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.keywords) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.registrationNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchDocuments(@Param("keyword") String keyword);
    
    // Поиск с пагинацией
    @Query("SELECT d FROM Document d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Document> searchDocuments(@Param("keyword") String keyword, Pageable pageable);
    
    
    // ===== СТАТИСТИКА =====
    
    // Количество документов по статусу
    long countByStatus(DocumentStatus status);
    
    // Количество документов по автору
    long countByAuthorId(Long authorId);
    
    // Количество документов по отделу
    long countByDepartmentId(Long departmentId);
    
    // Статистика по статусам
    @Query("SELECT d.status, COUNT(d) FROM Document d GROUP BY d.status")
    List<Object[]> getDocumentCountByStatus();
    
    // Статистика по типам документов
    @Query("SELECT dt.name, COUNT(d) FROM Document d JOIN d.documentType dt GROUP BY dt.name")
    List<Object[]> getDocumentCountByType();
    
    // Статистика по месяцам
    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', d.creationDate), COUNT(d) FROM Document d " +
           "WHERE d.creationDate BETWEEN :startDate AND :endDate GROUP BY FUNCTION('DATE_TRUNC', 'month', d.creationDate)")
    List<Object[]> getDocumentCountByMonth(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    
    // ===== КОМБИНИРОВАННЫЕ ФИЛЬТРЫ =====
    
    // Поиск по статусу и отделу
    List<Document> findByStatusAndDepartmentId(DocumentStatus status, Long departmentId);
    
    // Поиск по статусу и автору
    List<Document> findByStatusAndAuthorId(DocumentStatus status, Long authorId);
    
    // Поиск по автору и диапазону дат
    List<Document> findByAuthorIdAndCreationDateBetween(Long authorId, LocalDate startDate, LocalDate endDate);
    
}
