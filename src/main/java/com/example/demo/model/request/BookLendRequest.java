package com.example.demo.model.request;

import lombok.Data;
import java.util.List;

@Data
public class BookLendRequest {
    private List<Long> bookIds;
    private String username;
}
