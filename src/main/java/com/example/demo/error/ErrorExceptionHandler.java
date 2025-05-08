package com.example.demo.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler({
            BadRequestException.class,
            ConflictException.class,
            InvalidTokenException.class,
            NotFoundException.class,
            TokenCreationException.class,
            UnAuthorizedException.class,
            BusinessException.class
    })
    protected ResponseEntity<ErrorResponseDto> handleCustomException(BusinessException e) {
        return ErrorResponseDto.toResponseEntity(e);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDto> handleUnexpectedExceptions(Exception e) {
        return ErrorResponseDto.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}