package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "lend")
@NoArgsConstructor
public class Lend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private LendStatus status;
    // 시간을 타임스탬프 값으로
    private Instant startOn;
    private Instant dueOn;

    // 하나의 책을 여러 명이 대출
    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonManagedReference
    private Book book;

    // 한 명의 회원이 여러 권의 책 대출
    @ManyToOne // 여러 개의 Lend 가질 수 있는 Member 엔티티 참조
    // 엔티티 연관 관계 또는 Element Collection 연결하기 위한 column 지정
    // 엔티티의 필드가 데이터베이스 테이블의 FK와 매핑되도록
    @JoinColumn(name = "member_id")
    @JsonManagedReference
    private Member member;

    @Builder
    Lend (Member member, Book book, LendStatus status, Instant startOn, Instant dueOn) {
        this.member = member;
        this.book = book;
        this.status = status;
        this.startOn = startOn;
        this. dueOn = dueOn;
    }
}
