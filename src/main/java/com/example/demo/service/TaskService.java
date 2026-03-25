package com.example.demo.service;

import com.example.demo.dto.request.TaskCreateDto;
import com.example.demo.dto.request.TaskUpdateDto;
import com.example.demo.dto.response.TaskDto;
import com.example.demo.entity.*;
import com.example.demo.entity.TaskStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.jpa.DocumentJpaRepository;
import com.example.demo.repository.jpa.TaskJpaRepository;
import com.example.demo.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {
    
    private final TaskJpaRepository taskRepository;
    private final UserJpaRepository userRepository;
    private final DocumentJpaRepository documentRepository;
    
    /**
     * Конвертация Task в TaskDto
     */
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedDate(task.getCompletedDate());
        dto.setAssigneeName(task.getAssignee().getFullName());
        dto.setAssigneeId(task.getAssignee().getId());
        dto.setCreatorName(task.getCreator().getFullName());
        dto.setCreatorId(task.getCreator().getId());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getDocument() != null) {
            dto.setDocumentId(task.getDocument().getId());
            dto.setDocumentTitle(task.getDocument().getTitle());
        }
        
        return dto;
    }
    
    /**
     * Создание новой задачи
     */
    public TaskDto createTask(TaskCreateDto dto, Long creatorId) {
        log.info("Создание новой задачи: {}", dto.getTitle());
        
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Создатель не найден с id: " + creatorId));
        
        User assignee = userRepository.findById(dto.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Исполнитель не найден с id: " + dto.getAssigneeId()));
        
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setStatus(TaskStatus.NEW);
        task.setCreator(creator);
        task.setAssignee(assignee);
        
        if (dto.getDocumentId() != null) {
            Document document = documentRepository.findById(dto.getDocumentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + dto.getDocumentId()));
            task.setDocument(document);
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Задача создана с id: {}", savedTask.getId());
        
        return convertToDto(savedTask);
    }
    
    /**
     * Получение задачи по ID
     */
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + id));
        return convertToDto(task);
    }
    
    /**
     * Получение всех задач
     */
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач исполнителя
     */
    public List<TaskDto> getTasksByAssignee(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));
        
        return taskRepository.findByAssignee(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач, созданных пользователем
     */
    public List<TaskDto> getTasksByCreator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));
        
        return taskRepository.findByCreator(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач по статусу
     */
    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач по документу
     */
    public List<TaskDto> getTasksByDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден с id: " + documentId));
        
        return taskRepository.findByDocument(document)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач по приоритету
     */
    public List<TaskDto> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач на конкретную дату
     */
    public List<TaskDto> getTasksByDueDate(LocalDate dueDate) {
        return taskRepository.findByDueDate(dueDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение задач на сегодня
     */
    public List<TaskDto> getTasksDueToday() {
        return getTasksByDueDate(LocalDate.now());
    }
    
    /**
     * Получение просроченных задач
     */
    public List<TaskDto> getOverdueTasks() {
        return taskRepository.findOverdueTasks()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение активных задач (не завершенных и не отмененных)
     */
    public List<TaskDto> getActiveTasks() {
        return taskRepository.findActiveTasks()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Получение активных задач исполнителя
     */
    public List<TaskDto> getActiveTasksByAssignee(Long userId) {
        return taskRepository.findActiveTasksByAssignee(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновление задачи
     */
    public TaskDto updateTask(Long id, TaskUpdateDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + id));
        
        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate());
        }
        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Исполнитель не найден"));
            task.setAssignee(assignee);
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
            if (dto.getStatus() == TaskStatus.COMPLETED && task.getCompletedDate() == null) {
                task.setCompletedDate(LocalDate.now());
            }
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Задача {} обновлена", id);
        
        return convertToDto(updatedTask);
    }
    
    /**
     * Изменение статуса задачи
     */
    public TaskDto updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + id));
        
        task.setStatus(status);
        
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedDate(LocalDate.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Статус задачи {} изменен на {}", id, status);
        
        return convertToDto(updatedTask);
    }
    
    /**
     * Назначение исполнителя задачи
     */
    public TaskDto assignTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + taskId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));
        
        task.setAssignee(user);
        Task updatedTask = taskRepository.save(task);
        log.info("Задача {} назначена пользователю {}", taskId, user.getFullName());
        
        return convertToDto(updatedTask);
    }
    
    /**
     * Удаление задачи
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + id));
        
        taskRepository.delete(task);
        log.info("Задача с id {} удалена", id);
    }
    
    /**
     * Количество всех задач
     */
    public long getTaskCount() {
        return taskRepository.count();
    }
    
    /**
     * Количество задач по статусу
     */
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
    
    /**
     * Количество задач исполнителя
     */
    public long getTaskCountByAssignee(Long userId) {
        return taskRepository.countByAssigneeId(userId);
    }
}
