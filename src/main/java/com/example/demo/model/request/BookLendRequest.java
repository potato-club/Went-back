package com.example.demo.model.request;

import lombok.Data;

import java.util.List;

@Data
public class BookLendRequest {
    // 제네릭 클래스인 List의 타입 매개변수가 Long => Long 타입 객체만 저장 가능
    private List<Long> bookIds;
    private String username;
}
