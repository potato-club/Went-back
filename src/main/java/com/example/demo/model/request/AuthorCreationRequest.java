// DTO

package com.example.demo.model.request;

import lombok.Data;

// Getter, Setter, RequiredArgsConstructor, ToString, EqualsAndHashCode 어노테이션 포함
@Data
public class AuthorCreationRequest {
    private String firstName;
    private String lastName;
}
