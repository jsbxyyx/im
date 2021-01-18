package io.github.jsbxyyx.server.exception;

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

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
