package com.example.demo.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, 400, "Bad Request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal Server Error"),

    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, 4000, "The password is incorrect."),
    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, 4001, "Password must be at least 6 characters long"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, 4002, "Unsupported JWT Token"),

    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 4010, "Invalid Access Token"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 4011, "Invalid Refresh Token"),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, 4012, "Invalid JWT Signature"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 4013, "Expired JWT Token"),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, 4014, "Invalid Token Type"),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, 4015, "Invalid social login token"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 4040, "User Not Found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 4041, "Category Not Found"),

    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, 4091, "Username already exists"),

    ACCESS_TOKEN_NOT_CREATED(HttpStatus.INTERNAL_SERVER_ERROR, 5001, "Acccess Token Not Created"),
    REFRESH_TOKEN_NOT_CREATED(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "Refresh Token Not Created"),

    EXTERNAL_AUTH_PROVIDER_ERROR(HttpStatus.BAD_GATEWAY, 5020, "External authentication provider error");

    private final HttpStatus httpStatus;
    private final int statusCode;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int statusCode, String message) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.message = message;
    }
}