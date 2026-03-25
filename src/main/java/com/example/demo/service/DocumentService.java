package com.example.demo.service;

import com.example.demo.dto.request.DocumentCreateDto;
import com.example.demo.dto.response.DocumentDto;
import com.example.demo.entity.*;
import com.example.demo.entity.DocumentStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;


import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {
    
    private final DocumentJpaRepository documentRepository;
    private final UserJpaRepository userRepository;
    private final DepartmentJpaRepository departmentRepository;
    private final DocumentTypeJpaRepository documentTypeRepository;
    private final FileStorageService fileStorageService;
    
    /**
     * Генерация уникального регистрационного номера документа
     * Формат: ДОК-YYYY-XXXXX
     */
    private String generateRegistrationNumber() {
        String year = String.valueOf(Year.now().getValue());
        long count = documentRepository.count() + 1;
        String number = String.format("%05d", count);
        return "ДОК-" + year + "-" + number;
    }
    
    /**
     * Конвертация Document в DocumentDto
     */
    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setRegistrationNumber(document.getRegistrationNumber());
        dto.setDescription(document.getDescription());
        dto.setDocumentDate(document.getDocumentDate());
        dto.setCreationDate(document.getCreationDate());
        dto.setExpiryDate(document.getExpiryDate());
        dto.setStatus(document.getStatus());
        dto.setFileName(document.getFileName());
        dto.setFileSize(document.getFileSize());
        dto.setFileType(document.getFileType());
        dto.setAuthorName(document.getAuthor().getFullName());
        dto.setAuthorId(document.getAuthor().getId());
        dto.setKeywords(document.getKeywords());
        dto.setVersion(document.getVersion());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        
        if (document.getDocumentType() != null) {
            dto.setDocumentTypeName(document.getDocumentType().getName());
        }
        
        if (document.getDepartment() != null) {
            dto.setDepartmentName(document.getDepartment().getName());
        }
        
        return dto;
    }
    
    /**
     * Создание нового документа с файлом
     */
    public DocumentDto createDocument(DocumentCreateDto dto, MultipartFile file) {
        log.info("Создание нового документа: {}", dto.getTitle());
        
        // Сохраняем файл на диск
        String fileName = fileStorageService.storeFile(file);
        
        // Получаем автора
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Автор не найден с id: " + dto.getAuthorId()));
        
        // Создаем документ
        Document document = new Document();
        document.setTitle(dto.getTitle());
        document.setDescription(dto.getDescription());
        document.setRegistrationNumber(generateRegistrationNumber());
        document.setDocumentDate(dto.getDocumentDate() != null ? dto.getDocumentDate() : LocalDate.now());
        document.setCreationDate(LocalDate.now());
        document.setExpiryDate(dto.getExpiryDate());
        document.setStatus(DocumentStatus.CREATED);
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(fileName);
        document.setFileSize(file.getSize());
        document.setFileType(file.getContentType());
        document.setAuthor(author);
        document.setKeywords(dto.getKeywords());
        document.setVersion(dto.getVersion() != null ? dto.getVersion() : "1.0");
        
        // Устанавливаем тип документа, если указан
        if (dto.getDocumentTypeId() != null) {
            DocumentType documentType = documentTypeRepository.findById(dto.getDocumentTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Тип документа не найден с id: " + dto.getDocumentTypeId()));
            document.setDocumentType(documentType);
        }
        
        // Устанавливаем отдел, если указан
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Отдел не найден с id: " + dto.getDepartmentId()));
            document.setDepartment(department);
        } else {
            document.setDepartment(author.getDepartment());
        }
        
        Document savedDocument = documentRepository.save(document);
        log.info("Документ создан с id: {}", savedDocument.getId());
        
        return convertToDto(savedDocument);
    }
    
    /**
     * Получение документа по ID
     */
    public DocumentDto getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + id));
        return convertToDto(document);
    }
    
    /**
     * Получение всех документов с пагинацией
     */
    public Page<DocumentDto> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable).map(this::convertToDto);
    }
    
    /**
     * Поиск документов по ключевому слову
     */
    public List<DocumentDto> searchDocuments(String keyword) {
        return documentRepository.searchDocuments(keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение документов по статусу
     */
    public List<DocumentDto> getDocumentsByStatus(DocumentStatus status) {
        return documentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение документов по автору
     */
    public List<DocumentDto> getDocumentsByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Автор не найден"));
        
        return documentRepository.findByAuthor(author)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение документов по отделу
     */
    public List<DocumentDto> getDocumentsByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Отдел не найден"));
        
        return documentRepository.findByDepartment(department)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение документов по диапазону дат
     */
    public List<DocumentDto> getDocumentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return documentRepository.findByDocumentDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Изменение статуса документа
     */
    public DocumentDto updateDocumentStatus(Long id, DocumentStatus status) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + id));
        
        document.setStatus(status);
        Document updatedDocument = documentRepository.save(document);
        log.info("Статус документа {} изменен на {}", id, status);
        
        return convertToDto(updatedDocument);
    }
    
    /**
     * Обновление документа
     */
    public DocumentDto updateDocument(Long id, DocumentCreateDto dto, MultipartFile file) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + id));
        
        document.setTitle(dto.getTitle());
        document.setDescription(dto.getDescription());
        document.setDocumentDate(dto.getDocumentDate());
        document.setExpiryDate(dto.getExpiryDate());
        document.setKeywords(dto.getKeywords());
        document.setVersion(dto.getVersion());
        
        // Если загружен новый файл
        if (file != null && !file.isEmpty()) {
            // Удаляем старый файл
            fileStorageService.deleteFile(document.getFilePath());
            
            // Сохраняем новый
            String fileName = fileStorageService.storeFile(file);
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(fileName);
            document.setFileSize(file.getSize());
            document.setFileType(file.getContentType());
        }
        
        // Обновляем тип документа
        if (dto.getDocumentTypeId() != null) {
            DocumentType documentType = documentTypeRepository.findById(dto.getDocumentTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Тип документа не найден"));
            document.setDocumentType(documentType);
        }
        
        Document updatedDocument = documentRepository.save(document);
        return convertToDto(updatedDocument);
    }
    
    /**
     * Скачивание файла документа
     */
    public byte[] downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + id));
        
        try {
            Path filePath = fileStorageService.loadFile(document.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }
    
    /**
     * Удаление документа
     */
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + id));
        
        // Удаляем файл с диска
        fileStorageService.deleteFile(document.getFilePath());
        
        documentRepository.delete(document);
        log.info("Документ с id {} удален", id);
    }
    
    /**
     * Количество всех документов
     */
    public long getDocumentCount() {
        return documentRepository.count();
    }
    
    /**
     * Количество документов по статусу
     */
    public long getDocumentCountByStatus(DocumentStatus status) {
        return documentRepository.countByStatus(status);
    }
    
    /**
     * Получение просроченных документов
     */
    public List<DocumentDto> getExpiredDocuments() {
        return documentRepository.findExpiredDocuments()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение документов, истекающих в ближайшие дни
     */
    public List<DocumentDto> getDocumentsExpiringWithinDays(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        return documentRepository.findDocumentsExpiringBefore(endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
