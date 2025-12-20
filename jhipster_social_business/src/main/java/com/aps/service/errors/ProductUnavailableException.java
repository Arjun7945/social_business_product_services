package com.aps.service.errors;

public class ProductUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProductUnavailableException(String message) {
        super(message);
    }
}
