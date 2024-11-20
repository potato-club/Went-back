// 클라이언트에게 반환될 JSON 응답 형식 정의

package com.example.demo.error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Builder
@Data
public class ErrorResponseDTO {
    private int httpStatus;
    private int statusCode;
    private String message;

    public static ResponseEntity<ErrorResponseDTO> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(ErrorResponseDTO.builder()
                        .httpStatus(errorCode.getHttpStatus().value())
                        .statusCode(errorCode.getStatusCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
