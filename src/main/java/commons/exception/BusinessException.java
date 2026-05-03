package commons.exception;

import commons.result.ResultCode;

public class BusinessException extends RuntimeException {
    private final ResultCode code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BAD_REQUEST;
    }

    public BusinessException(ResultCode code, String message) {
        super(message);
        this.code = code;
    }

    public ResultCode getCode() { return code; }
}
