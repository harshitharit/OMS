package com.oms.exception;

public class PreferenceException extends RuntimeException {
    public PreferenceException(String message) {
        super(message);
    }

    public PreferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

