package by.gto.equipment.config;

import by.gto.equipment.model.AnswerStatusCode;
import by.gto.equipment.model.JSONResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        JSONResponse answer = new JSONResponse(AnswerStatusCode.COMMON_SYSTEM_ERROR,
                "Unhandled exception",
                ex.getStackTrace()
        );
        return new ResponseEntity<>(answer, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
