package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // JpaRepository에서 기본적으로 제공하는 기능 외에 추가적으로 구현하고 싶은 부분
    // => findBy로 시작하는 메서드 이름으로 쿼리를 요청하는 메서드임을 지정
    // ISBN이 DB에 존재하면 Optional 내부에 Book 객체 들어있게 됨
    // 해당하는 책 존재하지 않으면 Optional.empty() 반환
    Optional<Book> findByIsbn(String isbn);

    // Page<T> : 페이지 정보를 담게 되는 인터페이스
    // Pageable : 페이지 처리에 필요한 정보를 담게 되는 인터페이스
    Page<Book> findAll(Pageable pageable);
    Page<Book> findByIsbn(String isbn, Pageable pageable);

    // Pageable 객체를 받아 페이지네이션 적용한 쿼리를 실행 (요청한 페이지의 데이터만을 포함하도록) => 쿼리 결과는 Slice<Book> 형태
    // SELECT book => Book 엔티티의 모든 속성을 선택
    // FROM Book book => Book 엔티티를 조회
    // ORDER BY book.id ASC => id 기준으로 오름차순 정렬
    @Query("SELECT book FROM Book book ORDER BY book.id ASC")
    Slice<Book> findBooksAscending(Pageable pageable);

    // SELECT 컬럼명 FROM 테이블명 WHERE 조건 ORDER BY 컬럼명 ASC/DESC
    // WHERE book.id > :id => book.id가 쿼리 파라미터로 전달된 :id(쿼리 파라미터)보다 큰 Book 엔티티만을 선택
    // => 이전에 가져온 것보다 뒤에 있는 도서 가져오게 함
    @Query("SELECT book FROM Book book WHERE book.id > :id ORDER BY book.id ASC")
    Slice<Book> findBooksAscendingAfter(@Param("id") Long id, Pageable pageable);
}
