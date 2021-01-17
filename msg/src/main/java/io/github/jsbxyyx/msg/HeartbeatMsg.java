package io.github.jsbxyyx.msg;

/**
 * @author
 */
public class HeartbeatMsg implements MsgBody {

    private boolean ping;

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

    @Override
    public int getMsgType() {
        return MsgType.Heartbeat;
    }
}
