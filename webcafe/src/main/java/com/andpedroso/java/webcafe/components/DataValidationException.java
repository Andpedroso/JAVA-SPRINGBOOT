package com.andpedroso.java.webcafe.components;

import lombok.Getter;
import org.springframework.validation.BindingResult;

public class DataValidationException extends DataException {
    @Getter
    private BindingResult result;

    public DataValidationException(String message, BindingResult result) {
        super(message);
        this.result = result;
    }

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
