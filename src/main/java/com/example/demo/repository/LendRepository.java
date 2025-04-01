//package com.example.demo.repository;
//
//import com.example.demo.model.Book;
//import com.example.demo.model.Lend;
//import com.example.demo.model.LendStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface LendRepository extends JpaRepository<Lend, Long> {
//    Optional<Lend> findByBookAndStatus(Book book, LendStatus status);
//}
