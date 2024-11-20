package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String isbn;

    // N:1 => 한 저자가 여러 책 발간
    @ManyToOne
    @JoinColumn(name = "author_id") // 외래 키 매핑, name에는 참조하는 테이블의 기본 키 칼럼명
    @JsonManagedReference
    private Author author;

    // 하나의 책을 여러 명이 대출
    @JsonBackReference
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Lend> lends;

    @Builder
    Book (Long id, String name, String isbn, Author author) {
        this.id = id;
        this.name = name;
        this.isbn = isbn;
        this.author = author;
    }

    public void changeBookName(String isbn, String name) {
        this.isbn = isbn;
        this.name = name;
    }
}