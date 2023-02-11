package bot.exception;

public class ApiTelegramException extends RuntimeException {
    public ApiTelegramException(String message) {
        super(message);
    }
}
