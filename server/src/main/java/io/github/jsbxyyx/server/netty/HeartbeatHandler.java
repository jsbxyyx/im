package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.common.RemotingUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            NettyChannelManager.remove(ctx.channel());
//        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("channel inactive, " + RemotingUtil.parseChannelRemoteAddr(ctx.channel()));
        }
        NettyChannelManager.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.error("handler exception, " + RemotingUtil.parseChannelRemoteAddr(ctx.channel()), cause);
        } else {
            LOGGER.error("handler exception, " + RemotingUtil.parseChannelRemoteAddr(ctx.channel()), cause.getMessage());
        }
        NettyChannelManager.remove(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }

}
