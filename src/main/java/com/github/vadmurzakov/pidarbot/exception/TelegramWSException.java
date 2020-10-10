package com.github.vadmurzakov.pidarbot.exception;

public class TelegramWSException extends RuntimeException {
    public TelegramWSException(String message) {
        super(message);
    }
}
