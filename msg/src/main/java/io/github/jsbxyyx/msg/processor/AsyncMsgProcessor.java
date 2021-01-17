package io.github.jsbxyyx.msg.processor;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgBody;
import io.github.jsbxyyx.msg.type.MsgTypeProcessorFactory;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 * @since
 */
public class AsyncMsgProcessor implements MsgProcessor {
    @Override
    public byte type() {
        return Constants.ASYNC;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        MsgBody body = msg.getBody();
        return MsgTypeProcessorFactory.get(body.getMsgType()).handle(ctx, msg);
    }
}
