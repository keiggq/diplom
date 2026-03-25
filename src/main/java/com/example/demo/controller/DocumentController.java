package com.example.demo.controller;

import com.example.demo.dto.request.DocumentCreateDto;
import com.example.demo.dto.response.DocumentDto;
import com.example.demo.entity.DocumentStatus;
import com.example.demo.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    
    /**
     * Создание документа
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> createDocument(
            @RequestPart("document") DocumentCreateDto dto,
            @RequestPart("file") MultipartFile file) {
        
        DocumentDto createdDocument = documentService.createDocument(dto, file);
        return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
    }
    
    /**
     * Получение документа по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }
    
    /**
     * Получение всех документов с пагинацией
     */
    @GetMapping
    public ResponseEntity<Page<DocumentDto>> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(documentService.getAllDocuments(pageable));
    }
    
    /**
     * Поиск документов
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentDto>> searchDocuments(@RequestParam String keyword) {
        return ResponseEntity.ok(documentService.searchDocuments(keyword));
    }
    
    /**
     * Получение документов по статусу
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByStatus(@PathVariable DocumentStatus status) {
        return ResponseEntity.ok(documentService.getDocumentsByStatus(status));
    }
    
    /**
     * Получение документов по автору
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(documentService.getDocumentsByAuthor(authorId));
    }
    
    /**
     * Получение документов по отделу
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(documentService.getDocumentsByDepartment(departmentId));
    }
    
    /**
     * Получение документов по диапазону дат
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<DocumentDto>> getDocumentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(documentService.getDocumentsByDateRange(startDate, endDate));
    }
    
    /**
     * Изменение статуса документа
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<DocumentDto> updateDocumentStatus(
            @PathVariable Long id,
            @RequestParam DocumentStatus status) {
        
        return ResponseEntity.ok(documentService.updateDocumentStatus(id, status));
    }
    
    /**
     * Обновление документа
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> updateDocument(
            @PathVariable Long id,
            @RequestPart("document") DocumentCreateDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        return ResponseEntity.ok(documentService.updateDocument(id, dto, file));
    }
    
    /**
     * Скачивание файла
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        DocumentDto document = documentService.getDocumentById(id);
        byte[] fileContent = documentService.downloadDocument(id);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }
    
    /**
     * Удаление документа
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Количество документов
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getDocumentCount() {
        return ResponseEntity.ok(documentService.getDocumentCount());
    }
    
    /**
     * Количество документов по статусу
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getDocumentCountByStatus(@PathVariable DocumentStatus status) {
        return ResponseEntity.ok(documentService.getDocumentCountByStatus(status));
    }
    
    /**
     * Просроченные документы
     */
    @GetMapping("/expired")
    public ResponseEntity<List<DocumentDto>> getExpiredDocuments() {
        return ResponseEntity.ok(documentService.getExpiredDocuments());
    }
    
    /**
     * Документы, истекающие в ближайшие дни
     */
    @GetMapping("/expiring-within/{days}")
    public ResponseEntity<List<DocumentDto>> getDocumentsExpiringWithin(@PathVariable int days) {
        return ResponseEntity.ok(documentService.getDocumentsExpiringWithinDays(days));
    }
}
