package io.github.jsbxyyx.server.msg.type;

import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.*;
import io.github.jsbxyyx.server.exception.BasicException;
import io.github.jsbxyyx.server.netty.Global;
import io.github.jsbxyyx.server.netty.NettyChannelManager;
import io.github.jsbxyyx.server.service.User;
import io.github.jsbxyyx.server.service.UserService;
import io.netty.channel.Channel;
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
public class MsgForwardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgForwardService.class);

    public static String forward(Msg msg) {
        AnyMsg anyMsg = (AnyMsg) msg.getBody();
        String id = UUID.randomUUID().toString();
        String from = anyMsg.getFrom();
        String to = anyMsg.getTo();
        String toType = anyMsg.getToType();
        Date createTime = anyMsg.getCreateTime();

        AnyMsg forwardMsg = null;

        String content = null;
        if (anyMsg instanceof TextMsg) {
            content = ((TextMsg) anyMsg).getText();
            forwardMsg = new TextMsg();
            forwardMsg.setId(id);
            forwardMsg.setCreateTime(createTime);
            forwardMsg.setFrom(from);
            forwardMsg.setTo(to);
            forwardMsg.setToType(toType);
            ((TextMsg) forwardMsg).setText(content);
        } else if (anyMsg instanceof ImageMsg) {
            content = ((ImageMsg) anyMsg).getImage();
            forwardMsg = new ImageMsg();
            forwardMsg.setId(id);
            forwardMsg.setCreateTime(createTime);
            forwardMsg.setFrom(from);
            forwardMsg.setTo(to);
            forwardMsg.setToType(toType);
            ((ImageMsg) forwardMsg).setImage(content);
        } else {
            LOGGER.error("not support msg : {}", anyMsg.getClass());
            throw new BasicException(ErrorCode.MSG_NOT_SUPPORT);
        }

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
            m.setBody(anyMsg);
            channel.writeAndFlush(m);
        }
        return id;
    }

}
