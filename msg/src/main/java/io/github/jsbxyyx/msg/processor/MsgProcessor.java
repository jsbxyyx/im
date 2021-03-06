package io.github.jsbxyyx.msg.processor;

import io.github.jsbxyyx.msg.Msg;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 */
public interface MsgProcessor {

    byte type();

    Msg handle(ChannelHandlerContext ctx, Msg msg);

}
