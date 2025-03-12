package com.example.demo.error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Builder
@Data
public class ErrorResponseDto {
    private int httpStatus;
    private int statusCode;
    private String message;

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(ErrorResponseDto.builder()
                        .httpStatus(errorCode.getHttpStatus().value())
                        .statusCode(errorCode.getStatusCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
