package commons.exception;

import commons.result.ResultCode;

public class BusinessException extends RuntimeException {

    private final int code;
    private final ResultCode resultCode;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BAD_REQUEST.getCode();
        this.resultCode = ResultCode.BAD_REQUEST;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getDefaultMessage());
        this.code = resultCode.getCode();
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.resultCode = resultCode;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.resultCode = ResultCode.BUSINESS_ERROR;
    }

    public int getCode() {
        return code;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
