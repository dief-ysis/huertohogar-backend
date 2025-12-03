package com.huertohogar.exception;

public class WebpayException extends RuntimeException {
    public WebpayException(String message) {
        super(message);
    }
    public WebpayException(String message, Throwable cause) {
        super(message, cause);
    }
}