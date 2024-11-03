package com.example.demo.repository;

import com.example.demo.model.Book;
import com.example.demo.model.Lend;
import com.example.demo.model.LendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LendRepository extends JpaRepository<Lend, Long> {
    // 여러 필드 검색하고 싶으면 And로 연결 => Book, Status 필드 검색
    Optional<Lend> findByBookAndStatus(Book book, LendStatus status);
}
