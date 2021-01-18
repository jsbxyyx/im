package io.github.jsbxyyx.server.exception;

import io.github.jsbxyyx.msg.ErrorCode;

/**
 * @author
 * @since
 */
public class BasicException extends RuntimeException {

    private String code;
    private String message;

    public BasicException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BasicException(ErrorCode errorCode) {
        this.code = errorCode.code;
        this.message = errorCode.message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
