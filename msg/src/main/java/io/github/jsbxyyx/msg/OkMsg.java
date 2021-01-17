package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public class OkMsg implements MsgBody {
    @Override
    public int getMsgType() {
        return MsgType.Ok;
    }
}
