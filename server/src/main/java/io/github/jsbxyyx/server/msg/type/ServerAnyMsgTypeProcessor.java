package io.github.jsbxyyx.server.msg.type;

import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.*;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.github.jsbxyyx.server.exception.BasicException;
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
import java.util.UUID;

/**
 * @author
 * @since
 */
public class ServerAnyMsgTypeProcessor implements MsgTypeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAnyMsgTypeProcessor.class);

    @Override
    public int type() {
        return MsgType.Any;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        AnyMsg anyMsg = (AnyMsg) msg.getBody();
        String id = UUID.randomUUID().toString();
        String from = anyMsg.getFrom();
        String to = anyMsg.getTo();
        String toType = anyMsg.getToType();
        Date createTime = anyMsg.getCreateTime();
        byte[] content = anyMsg.getContent();
        int type = anyMsg.getType();

        if (!Objects.equals(toType, TextMsgToType.TO_TYPE_GROUP)) {
            throw new BasicException(ErrorCode.USER_TYPE_NOT_SUPPORT);
        }

        List<User> userList = UserService.getUserListByGroup(to);
        for (User user : userList) {
            if (Objects.equals(user.getUsername(), from)) {
                continue;
            }
            String channelId = Global.getChannelId(user.getUsername());
            if (StringUtil.isBlank(channelId)) {
                LOGGER.info("[{}] channel not online", user.getUsername());
                continue;
            }
            Channel channel = NettyChannelManager.get(channelId);
            if (channel == null) {
                LOGGER.info("[{}] channel not online", user.getUsername());
                continue;
            }
            Msg m = new Msg();
            m.setId(IdGenerator.getInstance().incrementAndGet());
            m.setType(msg.getType());
            m.setUsername(user.getUsername());
            m.setBody(new AnyMsg(id, from, to, toType, createTime, content, type));
            channel.writeAndFlush(m);
        }
        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), new IdMsg(id));
    }
}
