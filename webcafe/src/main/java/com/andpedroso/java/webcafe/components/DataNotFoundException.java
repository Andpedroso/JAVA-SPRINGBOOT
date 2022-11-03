package com.andpedroso.java.webcafe.components;

public class DataNotFoundException extends DataException {
    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
