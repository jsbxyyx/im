package io.github.jsbxyyx.msg;

/**
 * @author
 */
public class HeartbeatMsg implements MsgBody {

    public static final int TYPE = 0;
    private boolean ping;

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

}
