package by.gto.equipment.model;

public enum AnswerStatusCode {
    OK(0),
    COMMON_SYSTEM_ERROR(-1),
    COMMON_USER_ERROR(1);

    public final int errorCode;

    AnswerStatusCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
