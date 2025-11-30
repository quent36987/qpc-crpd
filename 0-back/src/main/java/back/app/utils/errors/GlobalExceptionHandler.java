package back.app.utils.errors;

import lombok.extern.slf4j.Slf4j;
import back.app.presentation.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorCode.class)
    @ResponseBody
    public ResponseEntity<MessageResponse> handleErrorCode(ErrorCode error) {
        HttpStatus status = HttpStatus.valueOf(error.getCode());
        MessageResponse body = new MessageResponse(error.getMessage());

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }


    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleThrowable(Throwable exception, WebRequest request) {
        HttpStatus status;
        String message;

        log.error("Handling exception: {}", exception.getMessage());

        if (exception instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            message = exception.getMessage();
        } else if (exception instanceof AuthenticationException) {
            status = HttpStatus.BAD_REQUEST;
            message = exception.getMessage();
        } else if (exception instanceof MethodArgumentNotValidException) {
            FieldError error = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError();
            status = HttpStatus.BAD_REQUEST;
            message = error != null ? error.getDefaultMessage() : "Invalid input";
        } else if (exception instanceof HttpMessageConversionException) {
            status = HttpStatus.BAD_REQUEST;
            message = "Invalid input";
        } else if (exception instanceof MaxUploadSizeExceededException) {
            status = HttpStatus.PAYLOAD_TOO_LARGE;
            message = "Maximum size exceeded";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal Server Error";
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status.value());
        responseBody.put("message", message);

        return new ResponseEntity<>(responseBody, status);
    }
}