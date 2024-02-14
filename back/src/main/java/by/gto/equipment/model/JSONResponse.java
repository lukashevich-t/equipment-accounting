package by.gto.equipment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONResponse {
    private int errCode;
    private String message;
    private Object content;
    private Integer flags;

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
