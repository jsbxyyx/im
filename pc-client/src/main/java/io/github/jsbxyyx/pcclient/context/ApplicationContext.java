package io.github.jsbxyyx.pcclient.context;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgBody;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.pcclient.netty.Global;
import io.github.jsbxyyx.pcclient.netty.NettyClient;
import io.github.jsbxyyx.pcclient.ui.MainUI;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author
 * @since
 */
public class ApplicationContext {

    private static NettyClient nettyClient;
    private static Channel channel;
    private static MainUI mainUI;

    public static void setNettyClient(NettyClient nc) {
        nettyClient = nc;
    }

    public static void setChannel(Channel ch) {
        channel = ch;
    }

    public static void setMainUI(MainUI mainUI) {
        ApplicationContext.mainUI = mainUI;
    }

    public static Msg sendSync(MsgBody msgBody) {
        checkNettyClient();
        Map<String, String> headMap = new HashMap<>();
        if (Global.getToken() != null) {
            headMap.put("token", Global.getToken());
        }
        Msg msg = Msg.build(IdGenerator.getInstance().incrementAndGet(), Constants.SYNC, headMap, msgBody);
        try {
            Msg rsp = (Msg) nettyClient.sendSync(channel, msg);
            return rsp;
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendAsync(MsgBody msgBody) {
        checkNettyClient();
        Map<String, String> headMap = new HashMap<>();
        if (Global.getToken() != null) {
            headMap.put("token", Global.getToken());
        }
        Msg msg = Msg.build(IdGenerator.getInstance().incrementAndGet(), Constants.ASYNC, headMap, msgBody);
        nettyClient.sendAsync(channel, msg);
    }

    public static void appendMsg(TextMsg textMsg) {
        if (mainUI == null) {
            throw new IllegalStateException("mainUI is null");
        }
        mainUI.appendMsg(textMsg);
    }

    private static void checkNettyClient() {
        if (nettyClient == null || channel == null) {
            throw new IllegalStateException("nettyClient or channel is null");
        }
    }

    public static void shutdown() {
        if (nettyClient != null) {
            nettyClient.shutdown();
        }
        Global.clear();
    }
}
