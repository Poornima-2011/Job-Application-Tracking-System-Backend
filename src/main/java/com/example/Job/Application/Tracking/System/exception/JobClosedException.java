package com.example.Job.Application.Tracking.System.exception;

public class JobClosedException extends RuntimeException {
    public JobClosedException(String message) {
        super(message);
    }
}