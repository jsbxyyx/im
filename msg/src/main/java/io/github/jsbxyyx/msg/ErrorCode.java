package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public enum ErrorCode {

    USER_NOT_FOUND("000001", "user not found"),
    USER_PASSWORD_INCORRECT("000002", "username or password incorrect"),
    ;
    public final String code;
    public final String message;
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
