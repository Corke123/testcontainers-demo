package com.github.corke123.userservice.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail handleDuplicateKeyException(DuplicateKeyException ignoredEx) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "User with given email already exists"
        );

        problemDetail.setTitle("Duplicate Resource");
        problemDetail.setType(URI.create("urn:problem:duplicate-resource"));

        return problemDetail;
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ProblemDetail handleTooManyRequests(HttpClientErrorException.TooManyRequests ignoredEx) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        problemDetail.setTitle("Rate Limit Exceeded");
        problemDetail.setDetail("You have exceeded the maximum number of requests.");
        return problemDetail;
    }
}
