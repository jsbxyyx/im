package io.github.jsbxyyx.server.msg.processor;

import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.LoginRequestMsg;
import io.github.jsbxyyx.msg.LoginResponseMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.processor.MsgProcessor;
import io.github.jsbxyyx.server.netty.Global;
import io.github.jsbxyyx.server.service.User;
import io.github.jsbxyyx.server.service.UserService;

import java.util.UUID;

/**
 * @author
 * @since
 */
public class LoginRequestMsgProcessor implements MsgProcessor {

    @Override
    public byte type() {
        return 0;
    }

    @Override
    public Msg handle(Msg msg) {
        LoginRequestMsg m = (LoginRequestMsg) msg.getBody();
        String username = m.getUsername();
        String password = m.getPassword();
        User user = UserService.login(username, password);
        LoginResponseMsg rmsg = new LoginResponseMsg();
        byte[] key = StringUtil.generateByteArray(16);
        String token = UUID.randomUUID().toString();
        Global.put(token, key);
        rmsg.setToken(token);
        rmsg.setAesKey(key);
        rmsg.setGroup(user.getGroup());
        rmsg.setGroupName(user.getGroupName());
        return Msg.build(msg.getId(), msg.getType(), msg.getHeadMap(), rmsg);
    }

}
