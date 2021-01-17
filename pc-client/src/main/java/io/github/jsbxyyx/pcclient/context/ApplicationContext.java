package io.github.jsbxyyx.pcclient.context;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgBody;
import io.github.jsbxyyx.pcclient.netty.Global;
import io.github.jsbxyyx.pcclient.netty.NettyClient;
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

    public static void setNettyClient(NettyClient nc) {
        nettyClient = nc;
    }

    public static NettyClient getNettyClient() {
        return nettyClient;
    }

    public static void setChannel(Channel ch) {
        channel = ch;
    }

    public static Channel getChannel() {
        return channel;
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
