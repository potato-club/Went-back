/*
    1. 예외 발생 => Spring이  @ControllerAdvice 또는 @RestControllerAdvice 클래스에서 해당 예외 처리할 @ExceptionHandler 찾음
    2. 일치하는 Handler 없다면, 개별 Controller에서 @ExceptionHandler 찾음
    3. 그래도 없으면 Spring 기본 예외 처리 로직 적용되어 기본 오류 페이지나 JSON 응답
 */

package com.example.demo.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {
    @ExceptionHandler(BusinessException.class) // CustomException 발생했을 때 호출되는 메서드
    protected ResponseEntity<ErrorResponseDTO> handleCustomException(BusinessException businessException) {
        return ErrorResponseDTO.toResponseEntity(businessException.getErrorCode());
    }
}