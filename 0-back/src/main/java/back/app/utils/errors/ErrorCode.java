package back.app.utils.errors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ErrorCode extends RuntimeException {
    private final int code;

    public ErrorCode(int code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;

        log.error("Error code: " + code + " - " + String.format(message, args));
    }

    public ErrorCode(Exception e, int code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;

        log.error("Error code: " + code + " - " + e.getMessage());
    }
}
