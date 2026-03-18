package com.example.demo.entity;

public enum TaskStatus {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    COMPLETED("Выполнена"),
    CANCELLED("Отменена"),
    OVERDUE("Просрочена");
    
    private final String displayName;
    
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
