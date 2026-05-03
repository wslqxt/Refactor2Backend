package commons.exception;

import commons.result.ResultCode;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED, message);
    }

    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
    }
}
