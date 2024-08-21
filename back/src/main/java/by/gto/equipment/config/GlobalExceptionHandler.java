package by.gto.equipment.config;

import by.gto.equipment.model.AnswerStatusCode;
import by.gto.equipment.model.JSONResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

//@ControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler(value = {Exception.class})
//    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
//        JSONResponse answer = new JSONResponse(AnswerStatusCode.COMMON_SYSTEM_ERROR, "", null);
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        if (ex instanceof NoResourceFoundException) {
//            status = HttpStatus.NOT_FOUND;
//            answer.withMessage("Not found");
//        } else if (ex instanceof AccessDeniedException) {
//            answer
//                    .withMessage("Access denied")
//                    .withHttpCode(HttpStatus.FORBIDDEN.value());
//        } else {
//            answer
//                    .withMessage("Unhandled Exception " + System.currentTimeMillis())
//                    .withContent(ex.getStackTrace());
//        }
//        return new ResponseEntity<>(answer.withHttpCode(status.value()), status);
//    }
//}
