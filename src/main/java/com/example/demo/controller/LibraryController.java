// HTTP 요청을 처리하는 컨트롤러 클래스 정의
// 클라이언트의 요청을 처리하기 위해 스프링은 가장 먼저, 해당하는 Controller 찾음
// <- 이때 @Controller라고 명시된 클래스를 탐색, Mapping 주소가 일치하는 메소드 내용 실행

// @RequestParam : HTTP 요청에서 전달된 쿼리 파라미터를 컨트롤러 메서드의 파라미터로 매핑 (쿼리 스트링 값)
//                  required = false ==> 클라이언트가 해당 파라미터를 제공하지 않아도 됨
// @PathVariable : URL에서 변수 값을 추출하여 메서드 파라미터에 할당
//                  근데 하나의 값만 받아올 수 있음
// @RequestBody : 요청 본문에서 HTTP 요청 바디(JSON 데이터)를 자바 객체로 변환하여 메서드 파라미터로 할당

// ResponseEntity : HTTPEntity 상속받아 구현한 클래스, 사용자의 HttpRequest에 대한 응답 데이터 포함
//                    ok() 메서드 => HTTP 200 Status Code와 함께 Response 생성 == 클라이언트 요청이 성공적으로 처리됨

package com.example.demo.controller;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Member;
import com.example.demo.model.request.*;
import com.example.demo.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
// 특정 URL로 요청을 보냈을 때, Controller의 어떤 메서드가 처리할지 매핑, value => 요청받을 URL 설정
@RequestMapping(value = "/api/library") 
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<Member> createMember (@RequestBody @Valid MemberCreationRequest memberCreationRequest) {
        Member newMember = libraryService.createMember(memberCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> loginMember (@RequestBody @Valid LoginRequest loginRequest) {
        String token = libraryService.login(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization","Bearer " + token)
                .body("Login Success♪♬");
    }

    // 도서 대출
    @PostMapping("/book/lend")
    public ResponseEntity<List<String>> lendABook (@RequestBody BookLendRequest bookLendRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String username = customUserDetails.getUsername();
        List<String> response = libraryService.lendABook(bookLendRequest);

        return ResponseEntity.ok(response);
    }
    
    // 관리자
    @GetMapping("/admin")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Hello, Administerator");
    }
    
    // 도서 조회
    @GetMapping("/book")
    public ResponseEntity readBooks (@RequestParam (value = "isbn", required = false) String isbn) { // isbn 파라미터를 포함할 수도 있고, 아닐 수도 있음
        if (isbn == null) {
            return ResponseEntity.ok(libraryService.readBooks()); // HTTP 응답 나타냄
        }
        return ResponseEntity.ok(libraryService.readBook(isbn));
    }

    @GetMapping("/book/{bookId}") // (엔드포인트{경로 변수}) 엔드포인트 == URL의 경로 부분
    public ResponseEntity<Book> readBook (@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(libraryService.readBook(bookId));
    }

    // 도서 조회
    @GetMapping("/book/page")
    public ResponseEntity<Page<Book>> readBooks
    // PageableDefault => page, size, sort, direction 설정
    (@RequestParam (value = "isbn", required = false) String isbn, @PageableDefault(size = 6) Pageable pageable) {
        if (isbn == null) {
            return ResponseEntity.ok(libraryService.readBooks(pageable));
        }
        return ResponseEntity.ok(libraryService.readBook(isbn, pageable));
    }

    // 도서 조회
    @GetMapping("/book/scroll")
    public ResponseEntity<Slice<Book>> readBooksAscending
    (@RequestParam(value = "cursor", required = false) Long cursor, @RequestParam(value = "size", defaultValue = "6") int size) {
        Slice<Book> books = libraryService.readBooksAscending(cursor, size);
        return ResponseEntity.ok(books);
    }

    // 도서 생성
    @PostMapping("/book")
    // JSON 형식의 데이터를 받아 BookCreationRequest request로 파라미터 할당
    public ResponseEntity<Book> createBook (@RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.createBook(request));
    }
    
    // 도서 부분 수정
    @PatchMapping("/book/{bookId}")
    public ResponseEntity<Book> updateBook (@PathVariable("bookId") Long bookId, @RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.updateBook(bookId, request));
    }
    
    // 저자 생성
    @PostMapping("/author")
    public ResponseEntity<Author> createAuthor (@RequestBody AuthorCreationRequest request) {
        return ResponseEntity.ok(libraryService.createAuthor(request));
    }
    
    // 저자 조회
    @GetMapping("/author")
    public ResponseEntity<List<Author>> readAuthors() {
        return ResponseEntity.ok(libraryService.readAuthors());
    }

    // 도서 ID에 해당하는 도서 삭제
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Void> deleteBook (@PathVariable("bookId") Long bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }
    
    // 회원 조회
    @GetMapping("/member")
    public ResponseEntity<List<Member>> readMembers() {
        return ResponseEntity.ok(libraryService.readMembers());
    }
    
    // 회원 부분 수정
    @PatchMapping("/member/{memberId}")
    public ResponseEntity<Member> updateMember (@RequestBody MemberCreationRequest request, @PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(libraryService.updateMember(memberId, request));
    }

}
