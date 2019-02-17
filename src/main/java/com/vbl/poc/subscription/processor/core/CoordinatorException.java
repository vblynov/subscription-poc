package com.vbl.poc.subscription.processor.core;

public class CoordinatorException extends RuntimeException {

    public CoordinatorException(Throwable cause) {
        super(cause);
    }

    public CoordinatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoordinatorException(String message) {
        super(message);
    }

}
