package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/repositories")
@RequiredArgsConstructor
public class TestRepositoryController {

    private final UserJpaRepository userRepository;
    private final DepartmentJpaRepository departmentRepository;
    private final DocumentTypeJpaRepository documentTypeRepository;
    private final DocumentJpaRepository documentRepository;
    private final TaskJpaRepository taskRepository;
    private final CommentJpaRepository commentRepository;

    @GetMapping("/check")
    public Map<String, Object> checkRepositories() {
        Map<String, Object> result = new HashMap<>();

        result.put("users_count", userRepository.count());
        result.put("departments_count", departmentRepository.count());
        result.put("document_types_count", documentTypeRepository.count());
        result.put("documents_count", documentRepository.count());
        result.put("tasks_count", taskRepository.count());
        result.put("comments_count", commentRepository.count());

        return result;
    }

    @PostMapping("/create-test-data")
    public String createTestData() {
        try {
            // 1. Создаем отдел
            Department dept = new Department();
            dept.setName("IT-отдел");
            dept.setCode("IT-001");
            dept.setDescription("Отдел информационных технологий");
            departmentRepository.save(dept);

            // 2. Создаем тип документа
            DocumentType docType = new DocumentType();
            docType.setName("Договор");
            docType.setPrefix("ДОГ");
            documentTypeRepository.save(docType);

            // 3. Создаем пользователя
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword("password123");
            user.setFullName("Тестовый Пользователь");
            user.setRole(Role.ROLE_USER);
            user.setDepartment(dept);
            userRepository.save(user);

            // 4. Создаем документ
            Document doc = new Document();
            doc.setTitle("Тестовый документ");
            doc.setRegistrationNumber("ДОГ-2024-0001");
            doc.setDocumentDate(LocalDate.now());
            doc.setCreationDate(LocalDate.now());
            doc.setStatus(DocumentStatus.CREATED);
            doc.setAuthor(user);
            doc.setDepartment(dept);
            doc.setDocumentType(docType);
            documentRepository.save(doc);

            // 5. Создаем задачу
            Task task = new Task();
            task.setTitle("Проверить документ");
            task.setDescription("Необходимо проверить тестовый документ");
            task.setStatus(TaskStatus.NEW);
            task.setPriority(TaskPriority.MEDIUM);
            task.setDueDate(LocalDate.now().plusDays(3));
            task.setAssignee(user);
            task.setCreator(user);
            task.setDocument(doc);
            taskRepository.save(task);

            return "✅ Тестовые данные созданы!";
        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @GetMapping("/documents")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/search/users")
    public List<User> searchUsers(@RequestParam String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    @GetMapping("/search/documents")
    public List<Document> searchDocuments(@RequestParam String keyword) {
        return documentRepository.searchDocuments(keyword);
    }

    @GetMapping("/statistics/documents")
    public List<Object[]> getDocumentStatistics() {
        return documentRepository.getDocumentCountByStatus();
    }

    @GetMapping("/overdue-tasks")
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks();
    }
}
