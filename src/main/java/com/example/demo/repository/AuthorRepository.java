// DB에 접근 가능한 객체
// CRUD 작업 수행, JPA를 통해 DB에서 데이터 가져오거나 저장

package com.example.demo.repository;

import com.example.demo.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    /* 
       JpaRepository => JPA에서 제공하는 메서드 사용 가능
       ㄴ 데이터베이스의 추가, 조회, 수정, 삭제 findAll(), findById(), save() 등
       JPA 엔티티에 대한 CRUD 작업을 자동으로 지원
       JpaRepository는 CrudRepository, ListCrudRepository, QueryByExampleExecutor 상속받음
       => 기본적인 CRUD 메서드 제공, 간편하게 CRUD 조작 가능

       Author : 엔티티 클래스 타입 지정 => 이 레포지토리가 Author 엔티티에 대한 작업을 수행
       Long : 엔티티의 기본 키 타입 지정 => Author 엔티티의 기본 키 필드가 Long 타입
     */
}
