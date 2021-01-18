package io.github.jsbxyyx.server.msg.type;

import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.msg.TextMsgToType;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.github.jsbxyyx.server.netty.Global;
import io.github.jsbxyyx.server.netty.NettyChannelManager;
import io.github.jsbxyyx.server.service.User;
import io.github.jsbxyyx.server.service.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * @since
 */
public class TextMsgTypeProcessor implements MsgTypeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextMsgTypeProcessor.class);

    @Override
    public int type() {
        return MsgType.Text;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {

        TextMsg tm = (TextMsg) msg.getBody();
        String from = tm.getFrom();
        String to = tm.getTo();
        String toType = tm.getToType();
        String text = tm.getText();
        Date createTime = tm.getCreateTime();

        if (!Objects.equals(toType, TextMsgToType.TO_TYPE_GROUP)) {
            return null;
        }

        List<User> userList = UserService.getUserListByGroup(to);
        for (User user : userList) {
            if (Objects.equals(user.getUsername(), from)) {
                continue;
            }
            Channel channel = NettyChannelManager.get(Global.getChannel(user.getUsername()));
            if (channel == null) {
                LOGGER.info("[{}] channel not online", user.getUsername());
                continue;
            }
            Msg m = new Msg();
            m.setId(IdGenerator.getInstance().incrementAndGet());
            m.setType(msg.getType());
            m.getHeadMap().put("username", user.getUsername());
            m.setBody(new TextMsg(from, to, toType, text, createTime));
            channel.writeAndFlush(m);
        }
        return null;
    }
}
