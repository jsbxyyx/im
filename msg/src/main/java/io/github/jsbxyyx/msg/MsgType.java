package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public class MsgType {
    public static final int Heartbeat = 0;
    public static final int LoginRequest = 1;
    public static final int LoginResponse = 2;
    public static final int Any = 3;
    public static int Error = 4;
    public static int Id = 5;

    public static final boolean encrypt(int msgType) {
        if (msgType == Any) {
            return true;
        }
        return false;
    }

    public static boolean decrypt(int msgType) {
        return encrypt(msgType);
    }
}
