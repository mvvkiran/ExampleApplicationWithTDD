package com.autoinsurance.quote.exception;

public class InvalidQuoteRequestException extends RuntimeException {
    
    public InvalidQuoteRequestException(String message) {
        super(message);
    }
    
    public InvalidQuoteRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}