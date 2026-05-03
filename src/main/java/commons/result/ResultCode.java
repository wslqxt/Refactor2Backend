package commons.result;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    EMAIL_EXISTS(1003, "邮箱已被注册"),
    EMAIL_NOT_REGISTERED(1004, "该邮箱未注册"),
    PASSWORD_ERROR(1005, "密码错误"),
    OLD_PASSWORD_ERROR(1006, "原密码错误"),

    RSA_ENCRYPT_ERROR(1101, "加密失败"),
    RSA_DECRYPT_ERROR(1102, "解密失败"),

    VALIDATION_ERROR(2001, "参数校验失败"),

    BUSINESS_ERROR(3001, "业务处理失败"),
    UNKNOWN_ERROR(4001, "未知错误");

    private final int code;
    private final String defaultMessage;

    ResultCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() { return code; }

    public String getDefaultMessage() { return defaultMessage; }
}
