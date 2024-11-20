package com.example.demo.model.request;

import lombok.Data;

@Data
public class AuthorCreationRequest {
    private Long id;
    private String firstName;
    private String lastName;
}
