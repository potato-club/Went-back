/*
    JPA : 자바의 ORM 기술을 쉽게 구현하도록 도와주는 API
    ORM : 객체의 필드와 데이터베이스의 데이터를 자동으로 매핑 해주는 것
            => 객체를 통해 간접적으로 데이터베이스 데이터 다루기 가능 (직관적인 코드로 데이터 조작)
*/
package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity // 클래스가 JPA 엔티티(DB와 연동되는 자바 객체)임을 나타냄 (데이터베이스 테이블과 연결될 클래스 설정)
@Table(name = "author") // 엔티티가 매핑될 데이터베이스 테이블의 이름을 지정
@NoArgsConstructor
public class Author {
    @Id // 엔티티 클래스의 필드를 데이터베이스 테이블의 기본 키로 지정
    // 기본 키 == 객체의 인스턴스를 구분하기 위한 키 값 => 테이블의 각 행 식별에 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값의 생성 전략 정의 (=> 엔티티의 식별자 값을 자동으로 생성)
    private Long id;
    private String firstName;
    private String lastName;

    @Builder
    public Author (Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @JsonBackReference // 필드를 JSON 직렬화에서 제외 => 무한 루프 방지
    // 엔티티 자체를 JSON으로 직렬화하여 반환할 경우 순환 참조 발생
    // @OneToMany => 1:N, 한 엔티티가 여러 개의 관련 엔티티를 가지는 경우
    // Book 객체가 Author 참조, 지연 로딩 설정, Author의 모든 연관 작업이 Book 객체에도 전파
    // Book 클래스에서 Author를 참조하는 author 필드가 연관 관계의 주인
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // Author 객체가 여러 개의 Book 객체와 연관 O
    private List<Book> books; // 제네릭 => 객체에 타입을 지정 => 값을 가져올 때 형변환 필요 X
}