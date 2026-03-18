package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_types")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DocumentType extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(unique = true, length = 10)
    private String prefix;  // Для генерации номера: ДОГ, СЧЕТ, АКТ
    
    @Column(length = 200)
    private String description;
    
    @OneToMany(mappedBy = "documentType")
    private List<Document> documents = new ArrayList<>();
}