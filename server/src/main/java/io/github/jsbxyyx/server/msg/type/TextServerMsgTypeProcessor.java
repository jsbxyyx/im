package io.github.jsbxyyx.server.msg.type;

import io.github.jsbxyyx.msg.IdMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 * @since
 */
public class TextServerMsgTypeProcessor implements MsgTypeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextServerMsgTypeProcessor.class);

    @Override
    public int type() {
        return MsgType.Text;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        String id = MsgForwardService.forward(msg);
        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), new IdMsg(id));
    }
}
