package com.columncalcrest.exception;

public class MaxIterationsExceededException extends RuntimeException{

    public MaxIterationsExceededException(String msg) {
        super(msg);
    }

    public MaxIterationsExceededException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
