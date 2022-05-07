package com.columncalcrest.exception;

public class ConcreteFailedException extends RuntimeException{

    public ConcreteFailedException(String msg) {
        super(msg);
    }

    public ConcreteFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
