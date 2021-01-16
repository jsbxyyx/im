package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public class LoginRequestMsg implements MsgBody {

    public static final int TYPE = 1;

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
