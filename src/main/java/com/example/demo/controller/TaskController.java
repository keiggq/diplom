package com.example.demo.controller;

import com.example.demo.dto.request.TaskCreateDto;
import com.example.demo.dto.request.TaskUpdateDto;
import com.example.demo.dto.response.TaskDto;
import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    @GetMapping("/my")
    public ResponseEntity<List<TaskDto>> getMyTasks(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId));
    }
    /**
     * Создание задачи
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskCreateDto dto,
            @RequestAttribute("userId") Long creatorId) {

        TaskDto createdTask = taskService.createTask(dto, creatorId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
        /**
     * Изменение статуса задачи
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestAttribute("userId") Long userId) {

        TaskDto updatedTask = taskService.updateStatus(id, status, userId);
        return ResponseEntity.ok(updatedTask);
    }
    /**
     * Получение задачи по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
    
    /**
     * Получение всех задач
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    /**
     * Получение задач исполнителя
     */
    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByAssignee(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId));
    }
    
    /**
     * Получение задач, созданных пользователем
     */
    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByCreator(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByCreator(userId));
    }
    
    /**
     * Получение задач по статусу
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }
    
    /**
     * Получение задач по приоритету
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable TaskPriority priority) {
        return ResponseEntity.ok(taskService.getTasksByPriority(priority));
    }
    
    /**
     * Получение задач по документу
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<TaskDto>> getTasksByDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(taskService.getTasksByDocument(documentId));
    }
    
    /**
     * Получение задач на дату
     */
    @GetMapping("/due-date")
    public ResponseEntity<List<TaskDto>> getTasksByDueDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return ResponseEntity.ok(taskService.getTasksByDueDate(date));
    }
    
    /**
     * Получение задач на сегодня
     */
    @GetMapping("/today")
    public ResponseEntity<List<TaskDto>> getTasksDueToday() {
        return ResponseEntity.ok(taskService.getTasksDueToday());
    }
    
    /**
     * Получение просроченных задач
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }
    
    /**
     * Получение активных задач
     */
    @GetMapping("/active")
    public ResponseEntity<List<TaskDto>> getActiveTasks() {
        return ResponseEntity.ok(taskService.getActiveTasks());
    }
    
    /**
     * Получение активных задач исполнителя
     */
    @GetMapping("/active/assignee/{userId}")
    public ResponseEntity<List<TaskDto>> getActiveTasksByAssignee(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getActiveTasksByAssignee(userId));
    }
    
    /**
     * Обновление задачи
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @RequestBody TaskUpdateDto dto) {
        
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }
    
    
    /**
     * Назначение исполнителя задачи
     */
    @PatchMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskDto> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        
        return ResponseEntity.ok(taskService.assignTask(taskId, userId));
    }
    
    /**
     * Удаление задачи
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Количество задач
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTaskCount() {
        return ResponseEntity.ok(taskService.getTaskCount());
    }
    
    /**
     * Количество задач по статусу
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getTaskCountByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.getTaskCountByStatus(status));
    }
    
    /**
     * Количество задач исполнителя
     */
    @GetMapping("/count/assignee/{userId}")
    public ResponseEntity<Long> getTaskCountByAssignee(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTaskCountByAssignee(userId));
    }
    
}
