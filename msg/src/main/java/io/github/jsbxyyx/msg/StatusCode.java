package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public enum StatusCode {

    OK("200"),
    BAD("400"),
    ERROR("500"),
    ;

    public final String code;
    StatusCode(String code) {
        this.code = code;
    }

}
