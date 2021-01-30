package io.github.jsbxyyx.pcclient.msg.type;

import io.github.jsbxyyx.msg.AnyMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 * @since
 */
public class ClientAnyMsgTypeProcessor implements MsgTypeProcessor {

    @Override
    public int type() {
        return MsgType.Any;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        AnyMsg body = (AnyMsg) msg.getBody();
        ApplicationContext.appendMsg(body);
        return null;
    }

}
