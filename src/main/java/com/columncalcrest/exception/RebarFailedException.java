package com.columncalcrest.exception;

public class RebarFailedException extends RuntimeException{

    public RebarFailedException(String msg) {
        super(msg);
    }

    public RebarFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
