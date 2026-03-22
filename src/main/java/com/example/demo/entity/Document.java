package com.example.demo.entity;

import com.example.demo.entity.DocumentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Document extends BaseEntity {
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(name = "registration_number", unique = true, nullable = false, length = 50)
    private String registrationNumber;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "document_date", nullable = false)
    private LocalDate documentDate;
    
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_type", length = 50)
    private String fileType;
    
    @Column(length = 500)
    private String keywords;
    
    @Column(length = 20)
    private String version;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;
    
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();
    
    // Вспомогательные методы
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setDocument(this);
    }
    
    public void addTask(Task task) {
        tasks.add(task);
        task.setDocument(this);
    }
    
    @PrePersist
    protected void onCreate() {
        super.onCreate();  // Вызов метода BaseEntity
        if (creationDate == null) {
            creationDate = LocalDate.now();
        }
        if (status == null) {
            status = DocumentStatus.CREATED;
        }
        if (version == null) {
            version = "1.0";
        }
    }
}