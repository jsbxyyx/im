package io.github.jsbxyyx.msg.type;

import io.github.jsbxyyx.msg.HeartbeatMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 */
public class HeartbeatMsgTypeProcessor implements MsgTypeProcessor {

    @Override
    public int type() {
        return MsgType.Heartbeat;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        HeartbeatMsg rspMsg = new HeartbeatMsg();
        rspMsg.setPing(false);
        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), rspMsg);
    }

}
