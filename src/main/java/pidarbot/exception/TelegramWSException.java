package pidarbot.exception;

public class TelegramWSException extends RuntimeException {
    public TelegramWSException(String message) {
        super(message);
    }
}
