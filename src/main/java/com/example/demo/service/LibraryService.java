// 엔티티를 사용하는 비즈니스 로직을 구현하는 서비스 클래스 정의
// 사용자의 요청에 따라 DB에 접근하여 데이터를 추가, 삭제, 수정, 선택과 같은 요청 처리

package com.example.demo.service;

import com.example.demo.error.ConflictException;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.model.*;
import com.example.demo.model.request.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// LibraryService 클래스가 Spring의 서비스 컴포넌트로 정의
// => Spring 컨테이너가 이 클래스를 관리 (앱 시작될 때 클래스를 인스턴스화하여 빈으로 등록)
@Service
@RequiredArgsConstructor
public class LibraryService {
    private final AuthorRepository authorRepository;
    private final LendRepository lendRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // 도서 생성
    public Book createBook (BookCreationRequest bookCreationRequest) {
        Author author = authorRepository.findById(bookCreationRequest.getAuthor().getId())
                .orElseThrow(() -> new NotFoundException("저자가 존재하지 않습니다.", ErrorCode.AUTHOR_NOT_FOUND));

        Book book = Book.builder()
                .name(bookCreationRequest.getName())
                .isbn(bookCreationRequest.getIsbn())
                .author(author)
                .build();

        return bookRepository.save(book);
    }

    // 저자 생성
    public Author createAuthor (AuthorCreationRequest authorCreationRequest) {
        Author author = Author.builder()
                .firstName(authorCreationRequest.getFirstName())
                .lastName(authorCreationRequest.getLastName())
                .build();
        return authorRepository.save(author);
    }

    // 도서 삭제
    public void deleteBook (Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND);
        }
        bookRepository.deleteById(id);
    }

    // 도서 대출
    public List<String> lendABook (BookLendRequest request, String username) {
        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        List<String> booksApprovedToBorrow = new ArrayList<>();

        request.getBookIds().forEach(bookId -> {
            // 책 정보 조회
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND));

            // 책이 현재 대출 중인지 확인
            Optional<Lend> borrowedBook = lendRepository.findByBookAndStatus(book, LendStatus.BORROWED);
            if (!borrowedBook.isPresent()) {
                booksApprovedToBorrow.add(book.getName());

                Lend lend = Lend.builder()
                        .member(member)
                        .book(book)
                        .status(LendStatus.BORROWED)
                        .startOn(Instant.now())
                        .dueOn(Instant.now().plus(30, ChronoUnit.DAYS))
                        .build();

                lendRepository.save(lend);
            } else {
                throw new ConflictException("이미 대출 중인 책입니다.", ErrorCode.BOOK_ALREADY_BORROWED);
            }
        });
        return booksApprovedToBorrow;
    }

    // 도서 수정
    public Book updateBook (Long bookId, BookCreationRequest bookCreationRequest) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND));

        book.changeBookName(bookCreationRequest.getIsbn(), bookCreationRequest.getName());
        return bookRepository.save(book);
    }

    // 회원 정보 수정
    public Member updateMember (MemberCreationRequest memberCreationRequest) {
        Member member = userRepository.findById(memberCreationRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        member.changeName(memberCreationRequest.getFirstName(), memberCreationRequest.getLastName());
        return userRepository.save(member);
    }

    // 도서 조회
    public List<Book> readBooks() {
        // 데이터베이스에서 모든 책을 조회하여 List<Book> 형식으로 반환
        return bookRepository.findAll();
    }

    public Book readBook (String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND));
    }

    public Book readBook (Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND));
    }

    // 저자 조회
    public List<Author> readAuthors() {
        return authorRepository.findAll();
    }

    // 회원 조회
    public List<Member> readMembers() {
        return userRepository.findAll();
    }

}