package com.example.demo.repository.jpa;

import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.entity.Document;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {
    
    // ===== БАЗОВЫЕ ПОИСКИ =====
    
    // Задачи исполнителя
    List<Task> findByAssignee(User assignee);
    
    // Задачи создателя
    List<Task> findByCreator(User creator);
    
    // Задачи по статусу
    List<Task> findByStatus(TaskStatus status);
    
    // Задачи по приоритету
    List<Task> findByPriority(TaskPriority priority);
    
    // Задачи по документу
    List<Task> findByDocument(Document document);
    
    // Задачи по документу (по ID)
    List<Task> findByDocumentId(Long documentId);
    
    
    // ===== ПОИСК ПО ДАТАМ =====
    
    // Задачи на конкретную дату
    List<Task> findByDueDate(LocalDate dueDate);
    
    // Задачи с дедлайном в диапазоне
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Просроченные задачи (дедлайн прошел, не выполнены)
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<Task> findOverdueTasks();
    
    // Задачи, истекающие сегодня
    @Query("SELECT t FROM Task t WHERE t.dueDate = CURRENT_DATE AND t.status != 'COMPLETED'")
    List<Task> findTasksDueToday();
    
    // Задачи, истекающие на этой неделе
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN CURRENT_DATE AND :endOfWeek AND t.status != 'COMPLETED'")
    List<Task> findTasksDueThisWeek(@Param("endOfWeek") LocalDate endOfWeek);
    
    
    // ===== КОМБИНИРОВАННЫЕ ПОИСКИ =====
    
    // Задачи исполнителя по статусу
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
    
    // Задачи исполнителя по приоритету
    List<Task> findByAssigneeIdAndPriority(Long assigneeId, TaskPriority priority);
    
    // Задачи по документу и статусу
    List<Task> findByDocumentIdAndStatus(Long documentId, TaskStatus status);
    
    
    // ===== СТАТИСТИКА =====
    
    // Количество задач по статусу
    long countByStatus(TaskStatus status);
    
    // Количество задач исполнителя
    long countByAssigneeId(Long assigneeId);
    
    // Количество задач по приоритету
    long countByPriority(TaskPriority priority);
    
    // Статистика по статусам
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> getTaskCountByStatus();
    
    // Статистика по приоритетам
    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> getTaskCountByPriority();
    
    // Статистика по исполнителям
    @Query("SELECT t.assignee.fullName, COUNT(t) FROM Task t GROUP BY t.assignee.fullName ORDER BY COUNT(t) DESC")
    List<Object[]> getTaskCountByAssignee();
    
    
    // ===== АКТИВНЫЕ ЗАДАЧИ =====
    
    // Активные задачи (не завершенные)
    @Query("SELECT t FROM Task t WHERE t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<Task> findActiveTasks();
    
    // Активные задачи исполнителя
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<Task> findActiveTasksByAssignee(@Param("userId") Long userId);
}
