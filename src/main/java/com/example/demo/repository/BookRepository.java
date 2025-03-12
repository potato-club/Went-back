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
    Optional<Book> findByIsbn(String isbn);

    Page<Book> findAll(Pageable pageable);
    Page<Book> findByIsbn(String isbn, Pageable pageable);

    @Query("SELECT book FROM Book book ORDER BY book.id ASC")
    Slice<Book> findBooksAscending(Pageable pageable);

    @Query("SELECT book FROM Book book WHERE book.id > :id ORDER BY book.id ASC")
    Slice<Book> findBooksAscendingAfter(@Param("id") Long id, Pageable pageable);
}
