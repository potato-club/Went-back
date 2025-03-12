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

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final AuthorRepository authorRepository;
    private final LendRepository lendRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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

    public Author createAuthor (AuthorCreationRequest authorCreationRequest) {
        Author author = Author.builder()
                .firstName(authorCreationRequest.getFirstName())
                .lastName(authorCreationRequest.getLastName())
                .build();
        return authorRepository.save(author);
    }

    public void deleteBook (Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND);
        }
        bookRepository.deleteById(id);
    }

    public List<String> lendABook (BookLendRequest request, String username) {
        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        List<String> booksApprovedToBorrow = new ArrayList<>();

        request.getBookIds().forEach(bookId -> {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND));

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

    public Book updateBook (Long bookId, BookCreationRequest bookCreationRequest) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다.", ErrorCode.BOOK_NOT_FOUND));

        book.changeBookName(bookCreationRequest.getIsbn(), bookCreationRequest.getName());
        return bookRepository.save(book);
    }

    public Member updateMember (MemberCreationRequest memberCreationRequest) {
        Member member = userRepository.findById(memberCreationRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        member.changeName(memberCreationRequest.getFirstName(), memberCreationRequest.getLastName());
        return userRepository.save(member);
    }

    public List<Book> readBooks() {
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

    public List<Author> readAuthors() {
        return authorRepository.findAll();
    }

    public List<Member> readMembers() {
        return userRepository.findAll();
    }

}