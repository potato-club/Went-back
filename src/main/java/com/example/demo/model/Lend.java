//package com.example.demo.model;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.Instant;
//
//@Getter
//@Entity
//@Table(name = "lend")
//@NoArgsConstructor
//public class Lend {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Enumerated(EnumType.ORDINAL)
//    private LendStatus status;
//    private Instant startOn;
//    private Instant dueOn;
//
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    @JsonManagedReference
//    private Book book;
//
//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    @JsonManagedReference
//    private Member member;
//
//    @Builder
//    Lend (Member member, Book book, LendStatus status, Instant startOn, Instant dueOn) {
//        this.member = member;
//        this.book = book;
//        this.status = status;
//        this.startOn = startOn;
//        this. dueOn = dueOn;
//    }
//}
