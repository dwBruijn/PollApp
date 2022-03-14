package com.pollapp.pollapp.payload.response;

import org.springframework.http.HttpStatus;

import java.util.List;

public class GenericAPIResponse {
    private HttpStatus status;
    private String message;
    private List errors;

    public GenericAPIResponse() {
    }

    public GenericAPIResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public GenericAPIResponse(HttpStatus status, String message, List errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List getErrors() {
        return errors;
    }

    public void setErrors(List errors) {
        this.errors = errors;
    }
}
