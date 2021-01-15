package io.github.jsbxyyx.server.netty;

/**
 * @author
 */
public class HeartbeatBusinessProcessor implements MsgProcessor {

    @Override
    public String type() {
        return HeartbeatMsg.TYPE;
    }

    @Override
    public void handle(Msg msg) {

    }

}
