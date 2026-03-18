package com.example.demo.entity;

public enum DocumentStatus {
    DRAFT("Черновик"),
    CREATED("Создан"),
    UNDER_REVIEW("На рассмотрении"),
    APPROVED("Утвержден"),
    REJECTED("Отклонен"),
    ARCHIVED("В архиве"),
    EXPIRED("Просрочен");
    
    private final String displayName;
    
    DocumentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
