package com.example.demo.repository.jpa;

import com.example.demo.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentTypeJpaRepository extends JpaRepository<DocumentType, Long> {
    
    // Поиск по названию
    Optional<DocumentType> findByName(String name);
    
    // Поиск по префиксу
    Optional<DocumentType> findByPrefix(String prefix);
    
    // Проверка существования названия
    boolean existsByName(String name);
    
    // Проверка существования префикса
    boolean existsByPrefix(String prefix);
}
