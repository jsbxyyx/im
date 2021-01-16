package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.ServiceErrorMsg;
import io.github.jsbxyyx.msg.StatusCode;
import io.github.jsbxyyx.msg.processor.MsgProcessorFactory;
import io.github.jsbxyyx.server.exception.BasicException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        Msg rsp;
        try {
            rsp = MsgProcessorFactory.get(msg.getType()).handle(msg);
        } catch (Throwable t) {
            if (t instanceof BasicException) {
                BasicException se = (BasicException) t;
                rsp = Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), new ServiceErrorMsg(se.getCode(), se.getMessage()), StatusCode.BAD);
            } else {
                rsp = Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), null, StatusCode.ERROR);
            }
        }
        if (rsp == null) {
            return ;
        }
        ctx.writeAndFlush(rsp);
    }
}
