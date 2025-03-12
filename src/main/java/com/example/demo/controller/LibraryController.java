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
@RequestMapping(value = "/api/library") 
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @PostMapping("/book/lend")
    public ResponseEntity<List<String>> lendABook (@RequestBody BookLendRequest bookLendRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String username = customUserDetails.getUsername();
        List<String> response = libraryService.lendABook(bookLendRequest, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book")
    public ResponseEntity readBooks (@RequestParam(value = "isbn", required = false) String isbn) {
        if (isbn == null) {
            return ResponseEntity.ok(libraryService.readBooks());
        }
        return ResponseEntity.ok(libraryService.readBook(isbn));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Book> readBook (@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(libraryService.readBook(bookId));
    }

    @GetMapping("/author")
    public ResponseEntity<List<Author>> readAuthors() {
        return ResponseEntity.ok(libraryService.readAuthors());
    }

    @PostMapping("/admin/book")
    public ResponseEntity<Book> createBook (@RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.createBook(request));
    }

    @PostMapping("/admin/author")
    public ResponseEntity<Author> createAuthor (@RequestBody AuthorCreationRequest request) {
        return ResponseEntity.ok(libraryService.createAuthor(request));
    }

    @DeleteMapping("/admin/book/{bookId}")
    public ResponseEntity<String> deleteBook (@PathVariable("bookId") Long bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.ok("The book deleted successfully.");
    }

    @GetMapping("/admin/member")
    public ResponseEntity<List<Member>> readMembers() {
        return ResponseEntity.ok(libraryService.readMembers());
    }

    @PatchMapping("/admin/book/{bookId}")
    public ResponseEntity<Book> updateBook (@PathVariable("bookId") Long bookId, @RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.updateBook(bookId, request));
    }

    @PatchMapping("/admin/member/{memberId}")
    public ResponseEntity<Member> updateMember (@RequestBody MemberCreationRequest request, @PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(libraryService.updateMember(request));
    }

}
