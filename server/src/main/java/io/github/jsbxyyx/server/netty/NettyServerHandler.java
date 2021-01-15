package io.github.jsbxyyx.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        MsgProcessorFactory.get(msg.getType()).handle(msg);
    }
}
