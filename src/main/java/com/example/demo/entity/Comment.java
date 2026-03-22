package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity {
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @JsonIgnore
    private Document document;
}