package io.github.jsbxyyx.server.msg.type;

import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.LoginRequestMsg;
import io.github.jsbxyyx.msg.LoginResponseMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgType;
import io.github.jsbxyyx.msg.type.MsgTypeProcessor;
import io.github.jsbxyyx.server.netty.EncryptionFactory;
import io.github.jsbxyyx.server.netty.Global;
import io.github.jsbxyyx.server.netty.NettyChannelManager;
import io.github.jsbxyyx.server.service.User;
import io.github.jsbxyyx.server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 * @since
 */
public class ServerLoginRequestMsgTypeProcessor implements MsgTypeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerLoginRequestMsgTypeProcessor.class);

    @Override
    public int type() {
        return MsgType.LoginRequest;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        LoginRequestMsg requestMsg = (LoginRequestMsg) msg.getBody();
        String username = requestMsg.getUsername();
        String password = requestMsg.getPassword();
        User findUser = UserService.login(username, password);
        LoginResponseMsg responseMsg = new LoginResponseMsg();
        byte[] key = StringUtil.generateByteArray(16);
        String token = EncryptionFactory.encodeToken(username);
        responseMsg.setToken(token);
        responseMsg.setAesKey(key);
        responseMsg.setGroup(findUser.getGroup());
        responseMsg.setGroupName(findUser.getGroupName());

        NettyChannelManager.add(ctx.channel());
        // token --> key
        Global.putToken(username, key);
        // username --> channel
        Global.putUserChannel(username, ctx.channel().id().asLongText());

        UserService.online(findUser.getGroup(), findUser);

        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), responseMsg);
    }


}
