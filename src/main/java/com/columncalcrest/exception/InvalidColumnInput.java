package com.columncalcrest.exception;

public class InvalidColumnInput extends RuntimeException{

    public InvalidColumnInput(String msg) {
        super(msg);
    }

    public InvalidColumnInput(String msg, Throwable cause) {
        super(msg, cause);
    }
}
