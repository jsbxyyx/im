package io.github.jsbxyyx.pcclient.msg.type;

import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 * @since
 */
public class TextMsgTypeProcessor implements MsgTypeProcessor {

    @Override
    public int type() {
        return MsgType.Text;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        return null;
    }

}
