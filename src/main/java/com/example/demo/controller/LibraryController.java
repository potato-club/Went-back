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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// 특정 URL로 요청을 보냈을 때, Controller의 어떤 메서드가 처리할지 매핑, value => 요청받을 URL 설정
@RequestMapping(value = "/api/library") 
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    // 도서 대출
    @PostMapping("/book/lend")
    public ResponseEntity<List<String>> lendABook (@RequestBody BookLendRequest bookLendRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String username = customUserDetails.getUsername();
        List<String> response = libraryService.lendABook(bookLendRequest, username);
        return ResponseEntity.ok(response);
    }

    // 도서 조회
    @GetMapping("/book")
    public ResponseEntity readBooks (@RequestParam (value = "isbn", required = false) String isbn) { // isbn 파라미터를 포함할 수도 있고, 아닐 수도 있음
        if (isbn == null) {
            return ResponseEntity.ok(libraryService.readBooks());
        }
        return ResponseEntity.ok(libraryService.readBook(isbn));
    }

    // 도서 조회 (ID)
    @GetMapping("/book/{bookId}") // (엔드포인트{경로 변수}) 엔드포인트 == URL의 경로 부분
    public ResponseEntity<Book> readBook (@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(libraryService.readBook(bookId));
    }

    // 저자 조회
    @GetMapping("/author")
    public ResponseEntity<List<Author>> readAuthors() {
        return ResponseEntity.ok(libraryService.readAuthors());
    }

    // 도서 생성
    @PostMapping("/admin/book")
    // JSON 형식의 데이터를 받아 BookCreationRequest request로 파라미터 할당
    public ResponseEntity<Book> createBook (@RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.createBook(request));
    }

    // 저자 생성
    @PostMapping("/admin/author")
    public ResponseEntity<Author> createAuthor (@RequestBody AuthorCreationRequest request) {
        return ResponseEntity.ok(libraryService.createAuthor(request));
    }

    // 도서 삭제
    @DeleteMapping("/admin/book/{bookId}")
    public ResponseEntity<String> deleteBook (@PathVariable("bookId") Long bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.ok("The book deleted successfully.");
    }

    // 회원 조회
    @GetMapping("/admin/member")
    public ResponseEntity<List<Member>> readMembers() {
        return ResponseEntity.ok(libraryService.readMembers());
    }

    // 도서 부분 수정
    @PatchMapping("/admin/book/{bookId}")
    public ResponseEntity<Book> updateBook (@PathVariable("bookId") Long bookId, @RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.updateBook(bookId, request));
    }

    // 회원 부분 수정
    @PatchMapping("/admin/member/{memberId}")
    public ResponseEntity<Member> updateMember (@RequestBody MemberCreationRequest request, @PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(libraryService.updateMember(request));
    }

}
