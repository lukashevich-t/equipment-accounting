package by.gto.equipment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONResponse {
    private int errCode;
    private String message;
    private Object content;
    private Integer flags;
    @JsonIgnore
    private int httpCode = HttpStatus.OK.value();

    private Date date = new Date();

//    private LocalDate localDate = LocalDate.now();
//
//    private LocalDateTime localDateTime = LocalDateTime.now();

    public JSONResponse withHttpCode(int httpCode) {
        this.httpCode = httpCode;
        return this;
    }

    public JSONResponse withErrCode(int errCode) {
        this.errCode = errCode;
        return this;
    }

    public JSONResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public JSONResponse withContent(Object content) {
        this.content = content;
        return this;
    }

    public JSONResponse withFlags(Integer flags) {
        this.flags = flags;
        return this;
    }

    public JSONResponse(AnswerStatusCode errorCode, String message, Object content, Integer flags) {
        this.errCode = errorCode.getErrorCode();
        this.message = message;
        this.content = content;
        this.flags = flags;
    }

    public JSONResponse(AnswerStatusCode errorCode, String message, Object content) {
        this(errorCode, message, content, null);
    }

    public JSONResponse(AnswerStatusCode errorCode, String message) {
        this(errorCode, message, null, null);
    }

    public JSONResponse(AnswerStatusCode errorCode) {
        this(errorCode, null, null, null);
    }
}
