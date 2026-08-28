package com.example.Job.Application.Tracking.System.exception;



public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}