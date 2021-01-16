package io.github.jsbxyyx.msg.processor;

import io.github.jsbxyyx.msg.HeartbeatMsg;
import io.github.jsbxyyx.msg.Msg;

/**
 * @author
 */
public class HeartbeatMsgProcessor implements MsgProcessor {

    @Override
    public byte type() {
        return HeartbeatMsg.TYPE;
    }

    @Override
    public Msg handle(Msg msg) {
        return null;
    }

}
