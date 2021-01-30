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

/**
 * @author
 * @since
 */
public class LoginRequestServerMsgTypeProcessor implements MsgTypeProcessor {

    @Override
    public int type() {
        return MsgType.LoginRequest;
    }

    @Override
    public Msg handle(ChannelHandlerContext ctx, Msg msg) {
        LoginRequestMsg m = (LoginRequestMsg) msg.getBody();
        String username = m.getUsername();
        String password = m.getPassword();
        User user = UserService.login(username, password);
        LoginResponseMsg rmsg = new LoginResponseMsg();
        byte[] key = StringUtil.generateByteArray(16);
        String token = EncryptionFactory.encodeToken(username);
        rmsg.setToken(token);
        rmsg.setAesKey(key);
        rmsg.setGroup(user.getGroup());
        rmsg.setGroupName(user.getGroupName());

        NettyChannelManager.add(ctx.channel());
        // token --> key
        Global.putToken(username, key);
        // username --> channel
        Global.putUserChannel(username, ctx.channel().id().asLongText());

        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), rmsg);
    }



}
