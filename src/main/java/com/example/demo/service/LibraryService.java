// 엔티티를 사용하는 비즈니스 로직을 구현하는 서비스 클래스 정의
// 사용자의 요청에 따라 DB에 접근하여 데이터를 추가, 삭제, 수정, 선택과 같은 요청 처리

package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.request.*;
import com.example.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.jwt.JwtGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// LibraryService 클래스가 Spring의 서비스 컴포넌트로 정의
// => Spring 컨테이너가 이 클래스를 관리 (앱 시작될 때 클래스를 인스턴스화하여 빈으로 등록)
@Service
// 초기화되지 않은 final 필드 생성자 자동 생성 => 의존성 주입 간편하게
// 새로운 필드를 추가할 때 다시 생성자를 만들어서 관리할 필요 X (@Autowired 사용하지 않고 의존성 주입)
@RequiredArgsConstructor
public class LibraryService {
    private final AuthorRepository authorRepository;
    private final LendRepository lendRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    // 회원 가입
    @Transactional
    public Member createMember (MemberCreationRequest memberCreationRequest) {
        if (userRepository.existsById(Long.valueOf(memberCreationRequest.getUsername()))) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (memberCreationRequest.getPassword().length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters long");
        }

        Member member = new Member();
        BeanUtils.copyProperties(memberCreationRequest, member);
        
        // 비밀번호 암호화하여 저장
        String encodedPassword = passwordEncoder.encode(memberCreationRequest.getPassword());
        member.setPassword(encodedPassword);
        member.setStatus(MemberStatus.ACTIVE);

        return userRepository.save(member);
    }
    
    // 로그인
    public String login(LoginRequest loginRequest) {
        // 사용자 정보 검증
        Member member = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("The username does not exist."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("The password is incorrect.");
        }

        // JWT 생성 및 반환
        return jwtGenerator.generateToken(member.getId()).getAccessToken();
    }

    public Member updateMember (Long id, MemberCreationRequest request) {
        Optional<Member> optionalMember = userRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new EntityNotFoundException("Member not present in the database");
        }
        Member member = optionalMember.get();
        member.setLastName(request.getLastName());
        member.setFirstName(request.getFirstName());
        return userRepository.save(member);
    }

    // 도서 조회
    public Book readBook (Long id) {
        // Optional => NullPointerException 방지
        // Optional<Book> => Optional 클래스가 Book 객체를 타입 매개변수로 받아서 만든 객체
        // 값을 가져올 때 형변환 필요 없이 Book 타입만, 근데 null 값 예외 처리하도록
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) { // Optional 객체에 저장된 값이 null인지 확인
            return book.get(); // Optional 객체가 감싸고 있는 것을 꺼냄
        }
        throw new EntityNotFoundException("Can't find any book under given ID");
    }

    // 도서 조회 
    public List<Book> readBooks() {
        // 데이터베이스에서 모든 책을 조회하여 List<Book> 형식으로 반환
        return bookRepository.findAll();
    }

    // 도서 조회
    public Page<Book> readBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book readBook (String isbn) {
        // 주어진 isbn으로 책 찾아 Optional<Book> 반환
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            return book.get();
        }
        throw new EntityNotFoundException("Can't find any book under given ISBN");
    }

    // 해당 isbn 도서 조회
    public Page<Book> readBook (String isbn, Pageable pageable) {
        return bookRepository.findByIsbn(isbn, pageable);
    }

    // 도서 조회
    // Slice => 데이터 양이 많고, 전체 페이지 개수 필요없는 경우
    public Slice<Book> readBooksAscending(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size); // PageRequest에 의해 Pageable에 페이징 정보 담겨서 객체화
        if (cursor == null) { // 초기 요청 => 오래된 도서부터 size만큼 가져옴
            return bookRepository.findBooksAscending(pageable);
        } else { // 커서 이후의 도서 size만큼 가져옴
            return bookRepository.findBooksAscendingAfter(cursor, pageable);
        }
    }

    public Book createBook (BookCreationRequest book) {
        // 책 저자 조회
        Optional<Author> author = authorRepository.findById(book.getAuthorId());
        if (!author.isPresent()) {
            throw new EntityNotFoundException("Author Not Found");
        }
        Book bookToCreate = new Book();
        BeanUtils.copyProperties(book, bookToCreate); // BookCreationRequest 객체의 속성들을 Book 객체로 복사
        bookToCreate.setAuthor(author.get()); // 조회한 저자를 새로 생성할 책의 저자 필드에 설정, AuthorRepository에서 조회한 Author 객체 반환
        return bookRepository.save(bookToCreate); // 책 DB에 저장, 저장된 책 객체 반환
    }

    public void deleteBook (Long id) {
        bookRepository.deleteById(id);
    }



    public Author createAuthor (AuthorCreationRequest request) {
        Author author = new Author();
        BeanUtils.copyProperties(request, author);
        return authorRepository.save(author);
    }

    public List<String> lendABook (BookLendRequest request) {
        Optional<Member> memberForId = userRepository.findById(request.getMemberId());
        if (!memberForId.isPresent()) {
            throw new EntityNotFoundException("Member not present in the database");
        }

        Member member = memberForId.get();
        if(member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("User is not active to proceed a lending");
        }

        List<String> booksApprovedToBorrow = new ArrayList<>();

        request.getBookIds().forEach(bookId -> {
            // 책이 DB에 존재하는지 확인
            Optional<Book> bookForId = bookRepository.findById(bookId);
            if (!bookForId.isPresent()) {
                throw new EntityNotFoundException("Can't find any book under given ID");
            }

            // 책이 현재 대출 중인지 확인
            Optional<Lend> borrowedBook = lendRepository.findByBookAndStatus(bookForId.get(), LendStatus.BORROWED);
            if (!borrowedBook.isPresent()) {
                booksApprovedToBorrow.add(bookForId.get().getName());
                Lend lend = new Lend();
                lend.setMember(memberForId.get());
                lend.setBook(bookForId.get());
                lend.setStatus(LendStatus.BORROWED);
                lend.setStartOn(Instant.now());
                lend.setDueOn(Instant.now().plus(30, ChronoUnit.DAYS));
                lendRepository.save(lend);
            }
        });
        return booksApprovedToBorrow;
    }

    public List<Author> readAuthors() {
        return authorRepository.findAll();
    }

    public Book updateBook (Long bookId, BookCreationRequest request) {
        Optional<Author> author = authorRepository.findById(request.getAuthorId());
        if (!author.isPresent()) {
            throw new EntityNotFoundException("Author Not Found");
        }
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            throw new EntityNotFoundException("Book Not Found");
        }
        Book book = optionalBook.get();
        book.setIsbn(request.getIsbn());
        book.setName(request.getName());
        book.setAuthor(author.get());
        return bookRepository.save(book);
    }

    public List<Member> readMembers() {
        return userRepository.findAll();
    }

}