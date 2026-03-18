package com.example.demo.entity;

public enum TaskPriority {
    HIGH("Высокий", 1),
    MEDIUM("Средний", 2),
    LOW("Низкий", 3);
    
    private final String displayName;
    private final int level;
    
    TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
}