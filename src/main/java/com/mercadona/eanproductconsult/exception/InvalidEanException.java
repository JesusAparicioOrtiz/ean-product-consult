package com.mercadona.eanproductconsult.exception;

public class InvalidEanException extends RuntimeException {
    
    public InvalidEanException(String message) {
        super(message);
    }
}
