package com.example.demo.entity;

public enum Role {
    ROLE_USER("Пользователь"),
    ROLE_MANAGER("Руководитель"),
    ROLE_ADMIN("Администратор");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
