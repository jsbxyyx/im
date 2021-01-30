package io.github.jsbxyyx.server.msg.type;

import io.github.jsbxyyx.msg.IdMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 * @since
 */
public class ImageServerMsgTypeProcessor implements MsgTypeProcessor {
    @Override
    public int type() {
        return MsgType.Image;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        String id = MsgForwardService.forward(msg);
        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), new IdMsg(id));
    }
}
