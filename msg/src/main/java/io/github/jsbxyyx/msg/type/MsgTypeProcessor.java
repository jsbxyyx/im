package io.github.jsbxyyx.msg.type;

import io.github.jsbxyyx.msg.Msg;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 * @since
 */
public interface MsgTypeProcessor {

    int type();

    Msg handle(ChannelHandlerContext ctx, Msg msg);

}
