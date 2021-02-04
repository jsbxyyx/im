package io.github.jsbxyyx.server.handler;

import io.github.jsbxyyx.server.netty.Global;
import io.github.jsbxyyx.server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author
 * @since
 */
public class OfflineHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String username = Global.getUsername(ctx.channel().id().asLongText());
        if (username != null) {
            UserService.offline(username);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String username = Global.getUsername(ctx.channel().id().asLongText());
        if (username != null) {
            UserService.offline(username);
        }
        super.exceptionCaught(ctx, cause);
    }

}
